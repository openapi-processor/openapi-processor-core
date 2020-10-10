/*
 * Copyright 2019-2020 the original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
 *
 * @author Martin Hauner
 * @author Bastian Wilhelm
 */
open class MethodWriter(
    private val /*val*/ apiOptions: ApiOptions,
    private val /*val*/ mappingAnnotationWriter: CoreMappingAnnotationWriter,
    private var /*val*/ parameterAnnotationWriter: CoreParameterAnnotationWriter,
    private val /*val*/ beanValidationFactory: BeanValidationFactory
) {

    fun write(target: Writer, endpoint: Endpoint, endpointResponse: EndpointResponse) {
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
                methodDefinition += " " + beanValidationFactory.createAnnotations(it.dataType)
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
