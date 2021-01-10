/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec

class ApiOptionsSpec: StringSpec({

    "throws when targetDir is invalid" {
        val options = ApiOptions()

        shouldThrow<InvalidOptionException> {
            options.validate()
        }
    }

})
