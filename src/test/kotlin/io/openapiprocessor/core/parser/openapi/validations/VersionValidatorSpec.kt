/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser.openapi.validations

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.openapiprocessor.core.parser.openapi.ValidationContext
import java.net.URL

class VersionValidatorSpec : StringSpec({
    val apiUrl = URL("memory:openapi.yaml")

    "validate 'openapi' value is a major.minor.patch version string" {
        val api = mapOf(
            "openapi" to "3.0.0"
        )

        val context = ValidationContext(apiUrl)
        val validator = VersionValidator()
        val messages = validator.validate(context, api)

        messages.isEmpty() shouldBe true
    }

    "fails to validate 'openapi' value if it is null" {
        val api = mapOf<String, Any?>(
            "openapi" to null
        )

        val context = ValidationContext(apiUrl)
        val validator = VersionValidator()
        val messages = validator.validate(context, api)

        messages.size shouldBe 1
        val message = messages.first()
        message.text shouldBe validator.message(null)
        message.path shouldBe "$.openapi"
    }

    "fails to validate 'openapi' value if it is not a major.minor.patch version string" {
        val api = mapOf(
            "openapi" to "bad"
        )

        val context = ValidationContext(apiUrl)
        val validator = VersionValidator()
        val messages = validator.validate(context, api)

        messages.size shouldBe 1
        val message = messages.first()
        message.text shouldBe validator.message("bad")
        message.path shouldBe "$.openapi"
    }

    "fails to validate 'openapi' value if it is not a string" {
        val api = mapOf(
            "openapi" to 5
        )

        val context = ValidationContext(apiUrl)
        val validator = VersionValidator()
        val messages = validator.validate(context, api)

        messages.size shouldBe 1
        val message = messages.first()
        message.text shouldBe validator.message(5)
        message.path shouldBe "$.openapi"
    }

})
