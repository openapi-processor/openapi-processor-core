/*
 * Copyright © 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.openapiprocessor.core.builder.api.endpoint
import io.openapiprocessor.core.converter.ApiOptions
import io.openapiprocessor.core.model.datatypes.CollectionDataType
import io.openapiprocessor.core.model.datatypes.StringDataType
import io.openapiprocessor.core.model.parameters.ParameterBase
import io.openapiprocessor.core.support.TestMappingAnnotationWriter
import io.openapiprocessor.core.support.TestParameterAnnotationWriter
import java.io.StringWriter

class MethodWriterSpec: StringSpec({
    isolationMode = IsolationMode.InstancePerTest

    val apiOptions = ApiOptions()

    val writer = MethodWriter (
        apiOptions,
        TestMappingAnnotationWriter(),
        TestParameterAnnotationWriter(),
        BeanValidationFactory())

    val target = StringWriter()

    "writes parameter validation annotation" {
        apiOptions.beanValidation = true

        val endpoint = endpoint("/foo") {
            parameters {
                any(object : ParameterBase("foo", StringDataType(), true) {
                })
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
            |    void getFoo(@NotNull @Parameter String foo);
            |
            """.trimMargin()
    }

    "writes multi content response methods with media-type postfix" {
        val endpoint = endpoint("/foo") {
            responses {
                status("200") {
                    response (
                        "application/json",
                        CollectionDataType(StringDataType())
                    )
                    response(
                        "application/xml",
                        CollectionDataType(StringDataType())
                    )
                }
            }
        }

        // when:
        writer.write (target, endpoint, endpoint.endpointResponses.first())
        writer.write (target, endpoint, endpoint.endpointResponses.last())

        // then:
        target.toString () shouldBe
            """    
            |    @CoreMapping
            |    Collection<String> getFooApplicationJson();
            |    @CoreMapping
            |    Collection<String> getFooApplicationXml();
            |
            """.trimMargin()
    }

    "writes multi content response methods with media-type postfix on operationId" {
        val endpoint = endpoint("/foo") {
            operationId = "get_foo_operation_id"
            responses {
                status("200") {
                    response (
                        "application/json",
                        CollectionDataType(StringDataType())
                    )
                    response(
                        "application/xml",
                        CollectionDataType(StringDataType())
                    )
                }
            }
        }

        // when:
        writer.write(target, endpoint, endpoint.endpointResponses.first())
        writer.write(target, endpoint, endpoint.endpointResponses.last())

        // then:
        target.toString () shouldBe
            """    
            |    @CoreMapping
            |    Collection<String> getFooOperationIdApplicationJson();
            |    @CoreMapping
            |    Collection<String> getFooOperationIdApplicationXml();
            |
            """.trimMargin()
    }

})
