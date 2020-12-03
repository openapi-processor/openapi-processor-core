/*
 * Copyright © 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.openapiprocessor.core.builder.api.endpoint
import io.openapiprocessor.core.converter.ApiOptions
import io.openapiprocessor.core.model.datatypes.StringDataType
import io.openapiprocessor.core.model.parameters.ParameterBase
import io.openapiprocessor.core.support.TestMappingAnnotationWriter
import io.openapiprocessor.core.support.TestParameterAnnotationWriter
import java.io.StringWriter

class MethodWriterSpec: StringSpec({

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

})
