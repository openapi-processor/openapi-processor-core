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

package com.github.hauner.openapi.core.writer.java

import io.openapiprocessor.core.model.parameters.Parameter
import io.openapiprocessor.core.writer.java.MappingAnnotationWriter as CoreMappingAnnotationWriter
import com.github.hauner.openapi.core.writer.java.ParameterAnnotationWriter as CoreParameterAnnotationWriter
import io.openapiprocessor.core.converter.ApiOptions
import io.openapiprocessor.core.model.Endpoint
import io.openapiprocessor.core.model.EndpointResponse
import io.openapiprocessor.core.support.Identifier

/**
 * Writer for Java interface methods, i.e. endpoints.
 *
 * @author Martin Hauner
 * @author Bastian Wilhelm
 */
class MethodWriter {

    ApiOptions apiOptions
    CoreMappingAnnotationWriter mappingAnnotationWriter
    CoreParameterAnnotationWriter parameterAnnotationWriter
    BeanValidationFactory beanValidationFactory

    void write (Writer target, Endpoint endpoint, EndpointResponse endpointResponse) {
        if (endpoint.deprecated) {
            target.write ("""\
    @Deprecated
""")
        }

        target.write ("""\
    ${createMappingAnnotation (endpoint, endpointResponse)}
    ${endpointResponse.responseType} ${createMethodName (endpoint, endpointResponse)}(${createParameters(endpoint)});
""")
    }

    private String createMappingAnnotation (Endpoint endpoint, EndpointResponse endpointResponse) {
        def annotation = new StringWriter ()
        mappingAnnotationWriter.write (annotation, endpoint, endpointResponse)
        annotation.toString ()
    }

    private String createMethodName (Endpoint endpoint, EndpointResponse endpointResponse) {
        if (endpoint.operationId != null) {
            return Identifier.toCamelCase (endpoint.operationId)
        }

        def tokens = endpoint.path.tokenize ('/')

        if (endpoint.hasMultipleEndpointResponses ()) {
            tokens += endpointResponse.contentType.tokenize ('/')
        }

        tokens = tokens.collect { Identifier.toCamelCase (it).capitalize () }
        def name = tokens.join ('')
        "${endpoint.method.method}${name}"
    }

    private String createParameters (Endpoint endpoint) {
        def ps = endpoint.parameters.collect {

            def methodDefinition = ''

            if (apiOptions.beanValidation) {
                methodDefinition += " ${beanValidationFactory.createAnnotations (it.dataType)}"
            }

            def annotation = createParameterAnnotation (it)
            if (! annotation.empty) {
                methodDefinition += " ${annotation}"
            }

            methodDefinition += " ${it.dataType.name} ${Identifier.toCamelCase (it.name)}"
            methodDefinition.trim()
        }

        if (!endpoint.requestBodies.empty) {
            def body = endpoint.requestBody
            def beanValidationAnnotations = ''
            if (apiOptions.beanValidation) {
                beanValidationAnnotations += " ${beanValidationFactory.createAnnotations (body.dataType)}"
            }
            def param = "${beanValidationAnnotations} ${createParameterAnnotation(body)} ${body.dataType.name} ${body.name}"
            ps.add (param.trim())
        }

        ps.join (', ')
    }

    private String createParameterAnnotation (Parameter parameter) {
        def annotation = new StringWriter ()
        if (parameter.deprecated) {
            annotation.write ("@Deprecated ")
        }
        parameterAnnotationWriter.write (annotation, parameter)
        annotation.toString ()
    }

}
