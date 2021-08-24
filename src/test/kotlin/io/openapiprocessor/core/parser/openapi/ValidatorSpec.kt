/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser.openapi

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.openapiprocessor.core.parser.openapi.validations.VersionValidator
import java.net.URL

class ValidatorSpec: StringSpec({

    "recognizes openapi 3.0.x" {
        val api = mapOf(
            "openapi" to "3.0.0"
        )

        val validator = Validator(URL("memory:openapi.yaml"), listOf(
            VersionValidator()
        ))

        val messages = validator.validate(api)

        messages.isEmpty() shouldBe true
    }

})
