/*
 * Copyright © 2019 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import io.openapiprocessor.core.converter.ApiOptions
import io.openapiprocessor.core.model.Endpoint
import io.openapiprocessor.core.model.EndpointResponse
import io.openapiprocessor.core.model.parameters.AdditionalParameter
import io.openapiprocessor.core.model.parameters.Parameter
import io.openapiprocessor.core.writer.java.MappingAnnotationWriter as CoreMappingAnnotationWriter
import io.openapiprocessor.core.writer.java.ParameterAnnotationWriter as CoreParameterAnnotationWriter
import java.io.StringWriter
import java.io.Writer

/**
 * Writer for Java interface methods, i.e. endpoints.
 */
open class MethodWriter(
    private val /*val*/ apiOptions: ApiOptions,
    private val /*val*/ mappingAnnotationWriter: CoreMappingAnnotationWriter,
    private var /*val*/ parameterAnnotationWriter: CoreParameterAnnotationWriter,
    private val /*val*/ beanValidationFactory: BeanValidationFactory,
    private val javadocWriter: JavaDocWriter = JavaDocWriter()
) {

    fun write(target: Writer, endpoint: Endpoint, endpointResponse: EndpointResponse) {
        if (apiOptions.javadoc) {
            target.write(
                javadocWriter.convert(endpoint, endpointResponse)
            )
        }

        if (endpoint.deprecated) {
            target.write (
                """
                |    @Deprecated
                |
                """.trimMargin())
        }

        target.write (
            """
            |    ${createMappingAnnotation (endpoint, endpointResponse)}
            |    ${endpointResponse.responseType} ${createMethodName (endpoint, endpointResponse)}(${createParameters(endpoint)});
            |
            """.trimMargin())
    }

    private fun createMappingAnnotation(endpoint: Endpoint, endpointResponse: EndpointResponse): String {
        val annotation = StringWriter()
        mappingAnnotationWriter.write(annotation, endpoint, endpointResponse)
        return annotation.toString ()
    }

    private fun createMethodName(endpoint: Endpoint, endpointResponse: EndpointResponse): String {
        if (endpoint.operationId != null) {
            return toCamelCase(endpoint.operationId)
        }

        val tokens = endpoint.path
            .split ('/')
            .filter { it.isNotEmpty() }
            .toMutableList()

        if (endpoint.hasMultipleEndpointResponses ()) {
            tokens += endpointResponse.contentType.split ('/')
        }

        val capitalized = tokens.map { toCamelCase(it).capitalize() }
        val name = capitalized.joinToString ("")

        return "${endpoint.method.method}${name}"
    }

    private fun createParameters(endpoint: Endpoint): String {
        val ps = endpoint.parameters.map {
            var methodDefinition = ""

            if (apiOptions.beanValidation) {
                methodDefinition += " " + beanValidationFactory
                    .createAnnotations(it.dataType, it.required)
            }

            val annotation = createParameterAnnotation (it)
            if (annotation.isNotEmpty()) {
                methodDefinition += " $annotation"
            }

            methodDefinition += " ${it.dataType.getName()} ${toCamelCase (it.name)}"
            methodDefinition.trim()
        }.toMutableList()

        if (endpoint.requestBodies.isNotEmpty()) {
            val body = endpoint.getRequestBody()
            var beanValidationAnnotations = ""
            if (apiOptions.beanValidation) {
                beanValidationAnnotations += " ${beanValidationFactory.createAnnotations (body.dataType)}"
            }
            val param = "$beanValidationAnnotations ${createParameterAnnotation(body)} ${body.dataType.getName()} ${body.name}"
            ps.add (param.trim())
        }

        return ps.joinToString (", ")
    }

    private fun createParameterAnnotation(parameter: Parameter): String {
        val annotation = StringWriter()
        if (parameter.deprecated) {
            annotation.write("@Deprecated ")
        }
        parameterAnnotationWriter.write(annotation, parameter)

        if (parameter is AdditionalParameter && parameter.annotationDataType != null) {
            annotation.write(" @${parameter.annotationDataType.getName()}${parameter.annotationDataType.getParameters()}")
        }

        return annotation.toString ()
    }

}
