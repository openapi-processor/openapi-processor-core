/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter

import io.openapiprocessor.core.converter.mapping.*
import io.openapiprocessor.core.converter.wrapper.MultiDataTypeWrapper
import io.openapiprocessor.core.converter.wrapper.ResultDataTypeWrapper
import io.openapiprocessor.core.converter.wrapper.SingleDataTypeWrapper
import io.openapiprocessor.core.framework.Framework
import io.openapiprocessor.core.model.*
import io.openapiprocessor.core.model.RequestBody as ModelRequestBody
import io.openapiprocessor.core.model.Response as ModelResponse
import io.openapiprocessor.core.model.parameters.Parameter as ModelParameter
import io.openapiprocessor.core.model.datatypes.*
import io.openapiprocessor.core.parser.*
import io.openapiprocessor.core.parser.RequestBody
import io.openapiprocessor.core.parser.Response
import io.openapiprocessor.core.writer.java.toClass
import org.slf4j.Logger
import org.slf4j.LoggerFactory

const val MULTIPART = "multipart/"
const val INTERFACE_DEFAULT_NAME = ""

/**
 * Converts the open api model to a new model that is better suited for generating source files
 * from the open api specification.
 */
class  ApiConverter(
    private val options: ApiOptions,
    private val framework: Framework
) {
    val log: Logger = LoggerFactory.getLogger(this.javaClass.name)

    private val mappingFinder = MappingFinder(options.typeMappings)
    private val dataTypeWrapper = ResultDataTypeWrapper(options, mappingFinder)
    private val dataTypeConverter = DataTypeConverter(options, mappingFinder)
    private val singleDataTypeWrapper = SingleDataTypeWrapper(options, mappingFinder)
    private val multiDataTypeWrapper = MultiDataTypeWrapper(options, mappingFinder)

    /**
     * converts the openapi model to the source generation model
     *
     * @param api the open api model
     * @return source generation model
     */
    fun convert(api: OpenApi): Api {
        val target = Api()
        createInterfaces(api, target)
        return target
    }

    private fun createInterfaces(api: OpenApi, target: Api) {
        val interfaces = hashMapOf<String, Interface>()

        api.getPaths().forEach { (path, pathValue) ->
            val operations = pathValue.getOperations()

            operations.forEach { op ->
                val itf = createInterface(path, op, interfaces)

                val ep = createEndpoint(path, op, target.getDataTypes(), api.getRefResolver())
                if (ep != null) {
                    itf.endpoints.add(ep)
                }
            }
        }

        target.setInterfaces(interfaces.values.map { it })
    }

    private fun createInterface(path: String, operation: Operation, interfaces: MutableMap<String, Interface>): Interface {
        val targetInterfaceName = getInterfaceName(operation, isExcluded(path, operation.getMethod()))

        var itf = interfaces[targetInterfaceName]
        if (itf != null) {
            return itf
        }

        itf = Interface(targetInterfaceName, listOf(options.packageName, "api").joinToString("."))

        interfaces.put (targetInterfaceName, itf)
        return itf
    }

    private fun createEndpoint(path: String, operation: Operation, dataTypes: DataTypes, resolver: RefResolver): Endpoint? {
        val ep = Endpoint(
            path,
            operation.getMethod(),
            operation.getOperationId(),
            operation.isDeprecated(),
            Documentation(
                summary = operation.summary,
                description = operation.description)
        )

        return try {
            collectParameters (operation.getParameters(), ep, dataTypes, resolver)
            collectRequestBody (operation.getRequestBody(), ep, dataTypes, resolver)
            collectResponses (operation.getResponses(), ep, dataTypes, resolver)
            ep.initEndpointResponses ()
            ep
        } catch (e: UnknownDataTypeException) {
            log.error ("failed to parse endpoint {} {} because of: '{}'", ep.path, ep.method, e.message, e)
            null
        }
    }

    private fun collectParameters(parameters: List<Parameter>, ep: Endpoint, dataTypes: DataTypes, resolver: RefResolver) {
        parameters.forEach { parameter ->
            ep.parameters.add (createParameter (ep, parameter, dataTypes, resolver))
        }

        val addMappings = getAdditionalParameter (ep)
        addMappings.forEach {
            ep.parameters.add (createAdditionalParameter (it, dataTypes, resolver))
        }
    }

    private fun getAdditionalParameter(ep: Endpoint): List<AddParameterTypeMapping> {
        // check endpoint parameter mappings
        val epMatch = mappingFinder.findEndpointAddParameterTypeMappings (ep.path, ep.method)
        if (epMatch.isNotEmpty())
            return epMatch

        // check global parameter mappings
        val paramMatch = mappingFinder.findAddParameterTypeMappings()
        if (paramMatch.isNotEmpty())
            return paramMatch

        return emptyList()
    }

    private fun collectRequestBody(requestBody: RequestBody?, ep: Endpoint, dataTypes: DataTypes, resolver: RefResolver) {
        if (requestBody == null) {
            return
        }

        requestBody.getContent().forEach { (contentType, mediaType) ->
            val info = SchemaInfo(
                SchemaInfo.Endpoint(ep.path, ep.method),
                getInlineRequestBodyName (ep.path, ep.method),
                "",
                mediaType.getSchema(),
                resolver)

            if (contentType.startsWith(MULTIPART)) {
                ep.parameters.addAll (createMultipartParameter(info, mediaType.encodings, dataTypes))
            } else {
                ep.requestBodies.add (createRequestBody (contentType, info, requestBody.getRequired(), dataTypes))
            }
        }
    }

    private fun collectResponses(responses: Map<String, Response>, ep: Endpoint, dataTypes: DataTypes, resolver: RefResolver) {
        responses.forEach { httpStatus, httpResponse ->
            val results = createResponses(
                ep,
                httpStatus,
                httpResponse,
                dataTypes,
                resolver)

            ep.addResponses (httpStatus, results)
        }
    }

    private fun createParameter(ep: Endpoint, parameter: Parameter, dataTypes: DataTypes, resolver: RefResolver): ModelParameter {
        val info = SchemaInfo (
            SchemaInfo.Endpoint(ep.path, ep.method),
            parameter.getName(),
            "",
            parameter.getSchema(),
            resolver)

        val dataType = convertDataType(info, dataTypes)

        return when (parameter.getIn()) {
            "query" ->
                framework.createQueryParameter (parameter, dataType)
            "path" ->
                framework.createPathParameter (parameter, dataType)
            "header" ->
                framework.createHeaderParameter (parameter, dataType)
            "cookie" ->
                framework.createCookieParameter (parameter, dataType)
            else ->
                // should not reach this, the openapi parser ignores parameters with unknown type.
                throw UnknownParameterTypeException(parameter.getName(), parameter.getIn())
        }
    }

    @Suppress("UNUSED_PARAMETER")
    private fun createAdditionalParameter(mapping: AddParameterTypeMapping, dataTypes: DataTypes, resolver: RefResolver): ModelParameter {
        val tm = mapping.getChildMappings().first () as TypeMapping
        val tt = tm.getTargetType()

        val addType = dataTypeConverter.createMappedDataType(tt)

        var annotationType: AnnotationDataType? = null
        if (mapping.annotation != null) {
            val at = TargetType(mapping.annotation.type, emptyList())

            annotationType = AnnotationDataType(
                at.getName(),
                at.getPkg(),
                mapping.annotation.parameters,
                mapping.annotation.parametersX
            )
        }

        val parameter = object: Parameter {

            override fun getIn(): String {
                return "add"
            }

            override fun getName(): String {
                return mapping.parameterName
            }

            override fun getSchema(): Schema {
                null!!
            }

            override fun isRequired(): Boolean {
                return true
            }

            override fun isDeprecated(): Boolean {
                return false
            }

            override val description: String?
                get() = null

        }

        return framework.createAdditionalParameter (parameter, addType, annotationType)
    }

    private fun createRequestBody(contentType: String, info: SchemaInfo, required: Boolean, dataTypes: DataTypes): ModelRequestBody {
        val dataType = convertDataType(info, dataTypes)
        val changedType = if (!info.isArray ()) {
            singleDataTypeWrapper.wrap(dataType, info)
        } else {
            multiDataTypeWrapper.wrap(dataType, info)
        }

        return framework.createRequestBody(contentType, required, changedType)
    }

    private fun createMultipartParameter(info: SchemaInfo, encodings: Map<String, Encoding>,
        dataTypes: DataTypes): Collection<ModelParameter> {
        val dataType = convertDataType(info, dataTypes)
        if (dataType !is ObjectDataType) {
            throw MultipartResponseBodyException(info.getPath())
        }

        dataTypes.del(dataType)
        val parameters = mutableListOf<ModelParameter>()
        dataType.forEach { property, propertyDataType ->
            val mpp = MultipartParameter(property, encodings[property]?.contentType)
            val parameter = framework.createMultipartParameter(mpp, propertyDataType)
            parameters.add(parameter)
        }
        return parameters
    }

    private fun createResponses(ep: Endpoint, httpStatus: String, response: Response, dataTypes: DataTypes, resolver: RefResolver): List<ModelResponse> {
        if (response.getContent().isEmpty()) {
            val info = SchemaInfo (
                SchemaInfo.Endpoint(ep.path, ep.method),
                "", "", null, resolver)

            val dataType = NoneDataType()
            val singleDataType = singleDataTypeWrapper.wrap (dataType, info)
            val resultDataType = dataTypeWrapper.wrap (singleDataType, info)

            return listOf(EmptyResponse (responseType = resultDataType))
        }

        val responses = mutableListOf<ModelResponse>()
        response.getContent().forEach { (contentType, mediaType) ->
            val schema = mediaType.getSchema()

            val info = SchemaInfo (
                SchemaInfo.Endpoint(ep.path, ep.method),
                getInlineResponseName (ep.path, ep.method, httpStatus),
                contentType,
                schema,
                resolver)

            val dataType = convertDataType(info, dataTypes)
            val changedType = if (!info.isArray ()) { // todo fails if ref
                singleDataTypeWrapper.wrap(dataType, info)
            } else {
                multiDataTypeWrapper.wrap(dataType, info)
            }
            val resultDataType = dataTypeWrapper.wrap(changedType, info)

            responses.add (ModelResponse(contentType, resultDataType, response.description))
        }

        return responses
    }

    private fun convertDataType(info: SchemaInfo, dataTypes: DataTypes): DataType {
        return dataTypeConverter.convert(info, dataTypes)
    }

    private fun getInlineRequestBodyName(path: String, method: HttpMethod): String {
        return toClass(path) + method.method.replaceFirstChar { it.uppercase() } + "RequestBody"
    }

    private fun getInlineResponseName(path: String, method: HttpMethod, httpStatus: String): String {
        return toClass(path) + method.method.replaceFirstChar { it.uppercase() } + "Response" + httpStatus
    }

    private fun isExcluded(path: String, method: HttpMethod): Boolean {
        return mappingFinder.isExcludedEndpoint(path, method)
    }

    private fun getInterfaceName(op: Operation, excluded: Boolean): String {
        var targetInterfaceName = INTERFACE_DEFAULT_NAME

        if((op.hasTags())) {
            targetInterfaceName = toClass(op.getFirstTag()!!)
        }

        if (excluded) {
            targetInterfaceName += "Excluded"
        }

        return targetInterfaceName
    }

}
