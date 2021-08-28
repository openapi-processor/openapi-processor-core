/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser.openapi4j

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.openapiprocessor.core.parser.ParserException
import io.openapiprocessor.test.stream.Memory

class ParserSpec : StringSpec({

    "catch parser resource exception" {
        val parser = Parser()

        shouldThrow<ParserException> {
            parser.parse("memory:openapi.yaml")
        }
    }

    "catch parser validation exception" {
        Memory.add("openapi.yaml", """
            openapi: 3.0.3
            info:
              title: missing path
              version: '1.0'
        """.trimIndent()
        )

        val parser = Parser()

        shouldThrow<ParserException> {
            parser.parse("memory:openapi.yaml")
        }
    }

    afterEach {
        Memory.clear()
    }

})

