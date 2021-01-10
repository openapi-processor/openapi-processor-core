/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec

class ParserSpec: StringSpec({

    "throws if apiPath is not set" {
        val parser = Parser()

        shouldThrow<NoOpenApiException> {
            parser.parse(emptyMap<String, Any>())
        }
    }

})
