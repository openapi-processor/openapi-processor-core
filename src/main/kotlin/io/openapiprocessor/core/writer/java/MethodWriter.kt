/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import io.openapiprocessor.core.converter.ApiOptions
import io.openapiprocessor.core.converter.MappingFinder
import io.openapiprocessor.core.converter.resultStyle
import io.openapiprocessor.core.model.Endpoint
import io.openapiprocessor.core.model.EndpointResponse
import io.openapiprocessor.core.model.parameters.AdditionalParameter
import io.openapiprocessor.core.model.parameters.Parameter
import io.openapiprocessor.core.support.capitalizeFirstChar
import io.openapiprocessor.core.writer.java.MappingAnnotationWriter as CoreMappingAnnotationWriter
import io.openapiprocessor.core.writer.java.ParameterAnnotationWriter as CoreParameterAnnotationWriter
import java.io.StringWriter
import java.io.Writer

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
    private val annotationWriter = AnnotationWriter()

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
            .joinToString("") { it.capitalizeFirstChar() }

        return head + tail
    }

    private fun createParameters(endpoint: Endpoint): String {
        val ps = endpoint.parameters.map {

            val dataTypeValue = if (apiOptions.beanValidation) {
                val info = beanValidationFactory.validate(it.dataType, it.required)
                info.inout.dataTypeValue
            } else {
                it.dataType.getTypeName()
            }

             "${createParameterAnnotation(endpoint, it)} $dataTypeValue ${toCamelCase (it.name)}".trim()
        }.toMutableList()

        if (endpoint.requestBodies.isNotEmpty()) {
            val body = endpoint.getRequestBody()

            val dataTypeValue = if (apiOptions.beanValidation) {
                val info = beanValidationFactory.validate(body.dataType, body.required)
                info.inout.dataTypeValue
            } else {
                body.dataType.getTypeName()
            }

            val param = "${createParameterAnnotation(endpoint, body)} $dataTypeValue ${body.name}"
            ps.add (param.trim())
        }

        return ps.joinToString (", ")
    }

    private fun createParameterAnnotation(endpoint: Endpoint, parameter: Parameter): String {
        val target = StringWriter()
        if (parameter.deprecated) {
            target.write("@Deprecated ")
        }

        parameterAnnotationWriter.write(target, parameter)

        val annotationTypeMappings = MappingFinder(apiOptions.typeMappings)
            .findParameterAnnotations(endpoint.path, endpoint.method, parameter.dataType.getTypeName())

        annotationTypeMappings.forEach {
            target.write(" ")
            annotationWriter.write(target, Annotation(it.annotation.type, it.annotation.parameters))
        }

        if (parameter is AdditionalParameter && parameter.annotationDataType != null) {
            target.write(" @${parameter.annotationDataType.getName()}")

            val parametersX = parameter.annotationDataType.getParameters()
            if (parametersX != null) {
                val parameters = mutableListOf<String>()

                parametersX.forEach {
                    if (it.key == "") {
                        parameters.add(it.value)
                    } else {
                        parameters.add("${it.key} = ${it.value}")
                    }
                }

                if (parameters.isNotEmpty()) {
                    target.write("(")
                    target.write(parameters.joinToString(", "))
                    target.write(")")
                }
            }
        }

        return target.toString()
    }
}
