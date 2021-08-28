/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser.swagger

import io.openapiprocessor.core.parser.OpenApi as ParserOpenApi
import io.openapiprocessor.core.parser.Path as ParserPath
import io.openapiprocessor.core.parser.RefResolver as ParserRefResolver
import io.swagger.v3.oas.models.PathItem as SwaggerPath
import io.swagger.v3.parser.core.models.SwaggerParseResult
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Swagger parser result.
 */
class OpenApi(private val result: SwaggerParseResult): ParserOpenApi {
    private val log: Logger = LoggerFactory.getLogger(this.javaClass.name)

    override fun getPaths(): Map<String, ParserPath> {
        val paths = linkedMapOf<String, ParserPath>()

        result.openAPI.paths.forEach { (name: String, value: SwaggerPath) ->
            paths[name] = Path(name, value)
        }

        return paths
    }

    override fun getRefResolver(): ParserRefResolver = RefResolver (result.openAPI)

    override fun printWarnings() {
        result.messages?.forEach {
            log.warn(it)
        }
    }

    override fun hasWarnings(): Boolean {
        return result.messages.isNotEmpty()
    }

}
