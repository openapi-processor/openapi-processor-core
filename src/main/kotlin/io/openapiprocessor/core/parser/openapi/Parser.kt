/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser.openapi

import io.openapiparser.*
import io.openapiprocessor.core.parser.OpenApi as ParserOpenApi
import io.openapiprocessor.core.parser.openapi.v30.OpenApi as ParserOpenApi30
import io.openapiprocessor.core.parser.openapi.v31.OpenApi as ParserOpenApi31
import io.openapiparser.jackson.JacksonConverter
import io.openapiparser.reader.UriReader
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.URI
import io.openapiparser.model.v30.OpenApi as OpenApi30
import io.openapiparser.model.v31.OpenApi as OpenApi31

/**
 * openapi-parser
 */
class Parser {
    private val log: Logger = LoggerFactory.getLogger(this.javaClass.name)

    fun parse(apiPath: String): ParserOpenApi {
        val baseUri = URI(apiPath)

        val resolver = ReferenceResolver(
            baseUri,
            UriReader(),
            JacksonConverter(),
            ReferenceRegistry()
        )

        val context = Context(baseUri, resolver)

        val parser = OpenApiParser(context)
        val result = parser.parse()

        when (result.version) {
            OpenApiResult.Version.V30 -> {
                return ParserOpenApi30(result.getModel(OpenApi30::class.java))
            }
            OpenApiResult.Version.V31 -> {
                return ParserOpenApi31(result.getModel(OpenApi31::class.java))
            }
            else -> {
                TODO() // unsupported openapi version
            }
        }

    }
}