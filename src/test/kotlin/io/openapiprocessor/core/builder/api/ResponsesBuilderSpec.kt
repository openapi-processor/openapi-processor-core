/*
 * Copyright © 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.builder.api

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.openapiprocessor.core.model.Response
import io.openapiprocessor.core.model.datatypes.StringDataType

class ResponsesBuilderSpec: StringSpec({

    "creates empty response" {
        val init: ResponsesBuilder.() -> Unit = {
        }

        // when:
        val responses = run(init)

        // then:
        responses.size shouldBe 0
    }

    "creates status response" {
        val init: ResponsesBuilder.() -> Unit = {
            status("200")
        }

        // when:
        val responses = run(init)

        // then:
        responses.size shouldBe 1
        responses["200"]?.isEmpty() shouldBe true
    }

    "creates multiple status responses" {
        val init: ResponsesBuilder.() -> Unit = {
            status("200")
            status("201")
            status("204")
        }

        // when:
        val responses = run(init)

        // then:
        responses.size shouldBe 3
        responses["200"]?.isEmpty() shouldBe true
        responses["201"]?.isEmpty() shouldBe true
        responses["204"]?.isEmpty() shouldBe true
    }

    "creates status response with contentType & dataType" {
        val init: ResponsesBuilder.() -> Unit = {
            status("200") {
                response("plain/text", StringDataType())
            }
        }

        // when:
        val responses = run(init)

        // then:
        val response = responses["200"]?.first()
        response?.contentType shouldBe "plain/text"
        response?.responseType should { it is StringDataType }
    }
})

fun run(init: ResponsesBuilder.() -> Unit): Map<String, List<Response>> {
    val builder = ResponsesBuilder()
    init(builder)
    return builder.build()
}
