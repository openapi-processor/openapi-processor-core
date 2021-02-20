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

const val MULTIPART = "multipart/form-data"
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
        val targetInterfaceName = getInterfaceName(operation, isExcluded(path))

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
            operation.description
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
            ep.parameters.add (createParameter (ep.path, parameter, dataTypes, resolver))
        }

        val addMappings = mappingFinder.findEndpointAddParameterTypeMappings (ep.path)
        addMappings.forEach {
            ep.parameters.add (createAdditionalParameter (ep.path, it as AddParameterTypeMapping, dataTypes, resolver))
        }
    }

    private fun collectRequestBody(requestBody: RequestBody?, ep: Endpoint, dataTypes: DataTypes, resolver: RefResolver) {
        if (requestBody == null) {
            return
        }

        requestBody.getContent().forEach { contentType, mediaType ->
            val info = SchemaInfo(
                ep.path,
                getInlineRequestBodyName (ep.path),
                "",
                mediaType.getSchema(),
                resolver)

            if (contentType == MULTIPART) {
                ep.parameters.addAll (createMultipartParameter(info, requestBody.getRequired()))
            } else {
                ep.requestBodies.add (createRequestBody (contentType, info, requestBody.getRequired(), dataTypes))
            }
        }
    }

    private fun collectResponses(responses: Map<String, Response>, ep: Endpoint, dataTypes: DataTypes, resolver: RefResolver) {
        responses.forEach { httpStatus, httpResponse ->
            val results = createResponses(
                ep.path,
                httpStatus,
                httpResponse,
                dataTypes,
                resolver)

            ep.addResponses (httpStatus, results)
        }
    }

    private fun createParameter(path: String, parameter: Parameter, dataTypes: DataTypes, resolver: RefResolver): ModelParameter {
        val info = SchemaInfo (
            path,
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

    private fun createAdditionalParameter(path: String, mapping: AddParameterTypeMapping, dataTypes: DataTypes, resolver: RefResolver): ModelParameter {
        val tm = mapping.getChildMappings().first () as TypeMapping
        val tt = tm.getTargetType()

        val addType = MappedDataType(
            tt.getName(),
            tt.getPkg(),
            tt.genericNames,
            null,
            false
        )

        var annotationType: AnnotationDataType? = null
        if (mapping.annotation != null) {
            val at = TargetType(mapping.annotation.type, emptyList())

            annotationType = AnnotationDataType(
                at.getName(),
                at.getPkg(),
                mapping.annotation.parameters
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

    private fun createMultipartParameter(info: SchemaInfo, required: Boolean): Collection<ModelParameter> {
        val dataType = convertDataType(info, DataTypes())
        if (! (dataType is ObjectDataType)) {
            throw MultipartResponseBodyException(info.getPath())
        }

        return dataType.getProperties().map {
            val parameter = object: Parameter {

                override fun getIn(): String {
                    return "multipart"
                }

                override fun getName(): String {
                    return it.key
                }

                override fun getSchema(): Schema {
                    null!!
                }

                override fun isDeprecated(): Boolean {
                    return false
                }

                override fun isRequired(): Boolean {
                    return true
                }

                override val description: String?
                    get() = null

            }

            framework.createMultipartParameter (parameter, it.value)
        }
    }

    private fun createResponses(path: String, httpStatus: String, response: Response, dataTypes: DataTypes, resolver: RefResolver): List<ModelResponse> {
        if (response.getContent().isEmpty()) {
            val info = SchemaInfo (path, "", "", null, resolver)

            val dataType = NoneDataType()
            val singleDataType = singleDataTypeWrapper.wrap (dataType, info)
            val resultDataType = dataTypeWrapper.wrap (singleDataType, info)

            return listOf(EmptyResponse (responseType = resultDataType))
        }

        val responses = mutableListOf<ModelResponse>()
        response.getContent().forEach { (contentType, mediaType) ->
            val schema = mediaType.getSchema()

            val info = SchemaInfo (
                path,
                getInlineResponseName (path, httpStatus),
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

    private fun getInlineRequestBodyName(path: String): String {
        return toClass(path) + "RequestBody"
    }

    private fun getInlineResponseName(path: String, httpStatus: String): String {
        return toClass(path) + "Response" + httpStatus
    }

    private fun isExcluded(path: String): Boolean {
        return mappingFinder.isExcludedEndpoint(path)
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
