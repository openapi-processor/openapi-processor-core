/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.openapiprocessor.core.builder.api.endpoint
import io.openapiprocessor.core.converter.ApiOptions
import io.openapiprocessor.core.converter.mapping.Annotation
import io.openapiprocessor.core.converter.mapping.AnnotationTypeMapping
import io.openapiprocessor.core.converter.mapping.EndpointTypeMapping
import io.openapiprocessor.core.converter.mapping.ParameterAnnotationTypeMapping
import io.openapiprocessor.core.model.datatypes.DataTypeName
import io.openapiprocessor.core.model.datatypes.ObjectDataType
import io.openapiprocessor.core.model.parameters.ParameterBase
import io.openapiprocessor.core.support.TestMappingAnnotationWriter
import io.openapiprocessor.core.support.TestParameterAnnotationWriter
import java.io.StringWriter

class MethodWriterAnnotationSpec: StringSpec ({
    isolationMode = IsolationMode.InstancePerTest

    val apiOptions = ApiOptions()

    val writer = MethodWriter (
        apiOptions,
        //AnnotationWriter()
        TestMappingAnnotationWriter(),
        TestParameterAnnotationWriter(),
        BeanValidationFactory())

    val target = StringWriter()


    "writes additional parameter annotation from annotation mapping" {
        apiOptions.typeMappings = listOf(
            ParameterAnnotationTypeMapping(
                AnnotationTypeMapping("Foo", annotation = Annotation(
                    "io.openapiprocessor.Bar", parametersX = linkedMapOf("bar" to "rab"))
                )
            )
        )

        val endpoint = endpoint("/foo") {
            parameters {
                any(object : ParameterBase("foo", ObjectDataType(
                    DataTypeName("Foo"), "pkg"), true) {})
            }
            responses {
                status("204") {
                    response()
                }
            }
        }

        // when:
        writer.write (target, endpoint, endpoint.endpointResponses.first ())

        // then:
        target.toString () shouldBe
            """    
            |    @CoreMapping
            |    void getFoo(@Parameter @Bar(bar = rab) Foo foo);
            |
            """.trimMargin()
    }

    "writes additional parameter annotation from path annotation mapping" {
        apiOptions.typeMappings = listOf(
            EndpointTypeMapping(
                "/foo", null, listOf(
                    ParameterAnnotationTypeMapping(
                        AnnotationTypeMapping("Foo", annotation = Annotation(
                            "io.openapiprocessor.Bar", parametersX = linkedMapOf("bar" to "rab"))
                        )
                    )
                )
            ))

        val endpoint = endpoint("/foo") {
            parameters {
                any(object : ParameterBase("foo", ObjectDataType(
                    DataTypeName("Foo"), "pkg"), true) {})
            }
            responses {
                status("204") {
                    response()
                }
            }
        }

        // when:
        writer.write (target, endpoint, endpoint.endpointResponses.first ())

        // then:
        target.toString () shouldBe
            """    
            |    @CoreMapping
            |    void getFoo(@Parameter @Bar(bar = rab) Foo foo);
            |
            """.trimMargin()
    }
})
