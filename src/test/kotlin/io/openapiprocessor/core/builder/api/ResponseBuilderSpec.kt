/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.builder.api

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.openapiprocessor.core.model.Response
import io.openapiprocessor.core.model.datatypes.NoneDataType
import io.openapiprocessor.core.model.datatypes.StringDataType

class ResponseBuilderSpec: StringSpec({

    "create response with content type" {
        val init: ResponseBuilder.() -> Unit = {
            response("text/plain")
        }

        // when:
        val responses = run(init)

        // then:
        responses.size shouldBe 1
        responses.first().contentType shouldBe "text/plain"
        (responses.first().responseType is NoneDataType) shouldBe true
    }

    "create response with content & data type" {
        val init: ResponseBuilder.() -> Unit = {
            response("text/html", StringDataType())
        }

        // when:
        val responses = run(init)

        // then:
        responses.size shouldBe 1
        responses.first().contentType shouldBe "text/html"
        responses.first().responseType should { it is StringDataType }
    }

    "create response with description" {
        val init: ResponseBuilder.() -> Unit = {
            response {
                description("response description")
            }
        }

        // when:
        val responses = run(init)

        // then:
        responses.size shouldBe 1
        responses.first().description shouldBe "response description"
    }

})

fun run(init: ResponseBuilder.() -> Unit): List<Response> {
    val builder = ResponseBuilder()
    init(builder)
    return builder.build()
}
