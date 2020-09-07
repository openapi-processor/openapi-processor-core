/*
 * Copyright © 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.builder.api

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.openapiprocessor.core.model.RequestBody
import io.openapiprocessor.core.model.datatypes.LongDataType
import io.openapiprocessor.core.model.datatypes.StringDataType
import io.openapiprocessor.core.model.parameters.Parameter
import io.openapiprocessor.core.model.parameters.ParameterBase
import kotlin.reflect.KClass

class ParametersBuilderSpec: StringSpec({

    "creates query parameter" {
        val init: ParametersBuilder.() -> Unit = {
            query("name", StringDataType())
        }

        // when:
        val parameters = run(init)

        // then:
        parameters.parameters().size shouldBe 1
        parameters.parameters().first().matches("name", StringDataType::class)
    }

    "creates query parameters" {
        val init: ParametersBuilder.() -> Unit = {
            query("foo", StringDataType())
            query("bar", LongDataType())
        }

        // when:
        val parameters = run(init)

        // then:
        parameters.parameters().size shouldBe 2
        parameters.parameters()[0].matches("foo", StringDataType::class)
        parameters.parameters()[1].matches("bar", LongDataType::class)
    }

    "creates any parameter" {
        val init: ParametersBuilder.() -> Unit = {
            any(object : ParameterBase("foo", StringDataType()) {
                override val withAnnotation: Boolean
                    get() = false
            })
        }

        // when:
        val parameters = run(init)

        // then:
        parameters.parameters().size shouldBe 1
        parameters.parameters().first().withAnnotation shouldBe false
    }

    "creates body parameter"  {
        val init: ParametersBuilder.() -> Unit = {
            body("body", "text/plain", StringDataType())
        }

        // when:
        val parameters = run(init)

        // then:
        parameters.bodies().size shouldBe 1
        parameters.bodies()[0].matches("body", "text/plain", StringDataType::class)
    }

})

private fun run(init: ParametersBuilder.() -> Unit): ParametersBuilder {
    val builder = ParametersBuilder()
    init(builder)
    return builder
}

private fun Parameter.matches(name: String, type: KClass<*>) {
    this.name shouldBe name
    this.dataType should { it::class.equals(type) }
}

private fun Parameter.matches(name: String, contentType: String, type: KClass<*>) {
    this as RequestBody
    this.name shouldBe name
    this.contentType shouldBe contentType
    this.dataType should { it::class.equals(type) }
}
