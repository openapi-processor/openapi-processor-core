/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.processor.mapping.v2

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.StringSpec
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import io.openapiprocessor.core.processor.MappingValidator

private fun String.fromResource(): String {
    return MappingValidator::class.java
        .getResourceAsStream(this)!!
        .readAllBytes()
        .decodeToString()
}

class MappingValidatorSpec: StringSpec({
    isolationMode = IsolationMode.InstancePerTest

    val validator = MappingValidator()

    "validates mapping.yaml with matching schema version" {
        forAll(
            row("v2"),
            row("v2.1")
        ) { v ->
            val yaml = """
                |openapi-processor-mapping: $v
                |
                |options:
                |  package-name: io.openapiprocessor.somewhere
            """.trimMargin()

            // when:
            val errors = validator.validate (yaml, v)

            // then:
            errors.shouldBeEmpty()
        }
    }

    "validates package-name option" {
        val yaml = """
                   |openapi-processor-mapping: v2
                   |
                   |options:
                   |  package-name: io.openapiprocessor.somewhere
                   """.trimMargin()

        // when:
        val errors = validator.validate (yaml, "v2")

        // then:
        errors.shouldBeEmpty()
    }

    "detects unknown top level property" {
        val yaml = """
                   |openapi-processor-mapping: v2
                   |
                   |options: {}
                   |
                   |bad:
                   |
                   """.trimMargin()

        // when:
        val errors = validator.validate (yaml, "v2")

        // then:
        errors.size shouldBe 1
        val error = errors.first()
        error.message shouldBe "\$.bad: is not defined in the schema and the schema does not allow additional properties"
    }

    "validates example mapping v2" {
        validator.validate("/mapping/v2/mapping.example.yaml".fromResource(), "v2").shouldBeEmpty()
    }

    "validates example mapping v2.1" {
        validator.validate("/mapping/v2.1/mapping.example.yaml".fromResource(), "v2.1").shouldBeEmpty()
    }
})
