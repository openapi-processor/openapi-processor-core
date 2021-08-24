/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser

import io.openapiprocessor.core.parser.swagger.Parser as Swagger
import io.openapiprocessor.core.parser.openapi.Parser as OpenApiParser
import io.openapiprocessor.core.parser.openapi4j.Parser as OpenApi4J
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * OpenAPI parser abstraction. Supports swagger or openapi4 parser.
 */
class Parser {
    private val log: Logger = LoggerFactory.getLogger(this.javaClass.name)

    fun parse(processorOptions: Map<String, *>): OpenApi {
        val apiPath: String = processorOptions["apiPath"]?.toString() ?: throw NoOpenApiException()

        when(val parser= processorOptions["parser"]?.toString()) {
            ParserType.SWAGGER.name -> {
                log.info("using SWAGGER parser")
                return Swagger().parse(apiPath)
            }
            ParserType.OPENAPI4J.name -> {
                log.info("using OPENAPI4J parser")
                return OpenApi4J().parse(apiPath)
            }
            ParserType.BUILTIN.name -> {
                log.info("using BUILTIN parser")
                return OpenApiParser().parse(apiPath)
            }
            else -> {
                if (parser != null) {
                    log.warn("unknown parser type: {}", parser)
                    log.warn("available parsers: SWAGGER, OPENAPI4J, BUILTIN")
                }
                return Swagger().parse(apiPath)
            }
        }
    }
}
