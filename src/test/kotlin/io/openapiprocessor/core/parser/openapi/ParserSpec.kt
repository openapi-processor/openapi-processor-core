/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser.openapi

import io.kotest.core.spec.style.StringSpec
import io.openapiprocessor.test.stream.Memory
import io.openapiprocessor.core.parser.openapi.v30.OpenApi as OpenApi30
import io.openapiprocessor.core.parser.openapi.v31.OpenApi as OpenApi31
import io.kotest.matchers.types.shouldBeInstanceOf

class ParserSpec: StringSpec({

    "recognizes openapi 3.0.x" {
        val parser = Parser()

        Memory.add("openapi.yaml", """
            openapi: 3.0.2
        """.trimIndent())

        val api = parser.parse("memory:openapi.yaml")

        api.shouldBeInstanceOf<OpenApi30>()
    }

    "recognizes openapi 3.1.x" {
        val parser = Parser()

        Memory.add("openapi.yaml", """
            openapi: 3.1.0
        """.trimIndent())

        val api = parser.parse("memory:openapi.yaml")

        api.shouldBeInstanceOf<OpenApi31>()
    }

})
