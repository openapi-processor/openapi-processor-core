/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import io.openapiprocessor.core.converter.ApiOptions
import io.openapiprocessor.core.converter.resultStyle
import io.openapiprocessor.core.model.Endpoint
import io.openapiprocessor.core.model.EndpointResponse
import io.openapiprocessor.core.model.parameters.AdditionalParameter
import io.openapiprocessor.core.model.parameters.Parameter
import io.openapiprocessor.core.writer.java.MappingAnnotationWriter as CoreMappingAnnotationWriter
import io.openapiprocessor.core.writer.java.ParameterAnnotationWriter as CoreParameterAnnotationWriter
import java.io.StringWriter
import java.io.Writer
import java.util.*

/**
 * Writer for Java interface methods, i.e. endpoints.
 */
open class MethodWriter(
    private val apiOptions: ApiOptions,
    private val mappingAnnotationWriter: CoreMappingAnnotationWriter,
    private var parameterAnnotationWriter: CoreParameterAnnotationWriter,
    private val beanValidationFactory: BeanValidationFactory,
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
            |    ${createMappingAnnotation(endpoint, endpointResponse)}
            |    ${createResult(endpointResponse)} ${createMethodName(endpoint, endpointResponse)}(${createParameters(endpoint)});
            |
            """.trimMargin())
    }

    private fun createMappingAnnotation(endpoint: Endpoint, endpointResponse: EndpointResponse): String {
        val annotation = StringWriter()
        mappingAnnotationWriter.write(annotation, endpoint, endpointResponse)
        return annotation.toString ()
    }

    private fun createResult(endpointResponse: EndpointResponse): String {
        return endpointResponse.getResponseType(apiOptions.resultStyle)
    }

    private fun createMethodName(endpoint: Endpoint, endpointResponse: EndpointResponse): String {
        val tokens: MutableList<String>

        if (endpoint.operationId != null) {
            tokens = mutableListOf(endpoint.operationId)
        } else {
            tokens = mutableListOf(endpoint.method.method)
            tokens += endpoint.path
                .split('/')
                .filter { it.isNotEmpty() }
                .toMutableList()
        }

        if (endpoint.hasMultipleEndpointResponses()) {
            tokens += endpointResponse.contentType.split('/')
        }

        val camel = tokens.map { toCamelCase(it) }
        val head = camel.first()
        val tail = camel.subList(1, camel.count())
            .joinToString("") { it.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() } }

        return head + tail
    }

    private fun createParameters(endpoint: Endpoint): String {
        val ps = endpoint.parameters.map {
            var methodDefinition = ""

            if (apiOptions.beanValidation) {
                val info = beanValidationFactory.validate(it.dataType, it.required)
                methodDefinition += " " + info.annotations.joinToString(" ")
            }

            val annotation = createParameterAnnotation (it)
            if (annotation.isNotEmpty()) {
                methodDefinition += " $annotation"
            }

            methodDefinition += " ${it.dataType.getTypeName()} ${toCamelCase (it.name)}"
            methodDefinition.trim()
        }.toMutableList()

        if (endpoint.requestBodies.isNotEmpty()) {
            val body = endpoint.getRequestBody()
            var beanValidationAnnotations = ""
            if (apiOptions.beanValidation) {
                val info = beanValidationFactory.validate(body.dataType, false)
                beanValidationAnnotations += " ${info.annotations.joinToString(" ")}"
            }
            val param = "$beanValidationAnnotations ${createParameterAnnotation(body)} ${body.dataType.getTypeName()} ${body.name}"
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
