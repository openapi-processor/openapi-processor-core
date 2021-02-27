/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser.openapi4j

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.openapiprocessor.core.parser.ParserType
import io.openapiprocessor.core.support.parse

class PathSpec : StringSpec({

    "operation contains endpoint level parameters" {
        val openApi = parse ("""
            openapi: 3.0.2
            info:
              title: parameter at endpoint
              version: 1.0.0
            
            paths:
            
              /foo:
                parameters:
                  - schema:
                      type: string
                    name: bar
                    in: query
                    required: true
            
                get:
                  responses:
                    '204':
                      description: empty
        """.trimIndent(), ParserType.OPENAPI4J)

        val path = openApi.getPaths()["/foo"]
        val op = path!!.getOperations().first()

        op.getParameters().shouldNotBeEmpty()
    }

})
