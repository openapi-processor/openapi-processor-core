/*
 * Copyright © 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.builder.api

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class ParameterBuilderSpec: StringSpec({

    "creates required parameter" {
        val init: ParameterBuilder.() -> Unit = {
            required()
        }

        // when:
        val builder = run(init)

        // then:
        builder.required shouldBe true
    }

    "creates deprecated parameter" {
        val init: ParameterBuilder.() -> Unit = {
            deprecated()
        }

        // when:
        val builder = run(init)

        // then:
        builder.deprecated shouldBe true
    }

})

fun run(init: ParameterBuilder.() -> Unit): ParameterBuilder {
    val builder = ParameterBuilder()
    init(builder)
    return builder
}
