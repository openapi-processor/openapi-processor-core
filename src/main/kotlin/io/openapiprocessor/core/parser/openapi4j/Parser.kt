/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser.openapi4j

import io.openapiprocessor.core.parser.ParserException
import io.openapiprocessor.core.support.toURL
import org.openapi4j.core.exception.ResolutionException
import org.openapi4j.core.validation.ValidationException
import io.openapiprocessor.core.parser.OpenApi as ParserOpenApi
import org.openapi4j.parser.OpenApi3Parser
import org.openapi4j.parser.validation.v3.OpenApi3Validator
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * openapi4j parser.
 */
open class Parser {
    private val log: Logger = LoggerFactory.getLogger(this.javaClass.name)

    fun parse(apiPath: String): ParserOpenApi {
        try {
            return run(apiPath)
        } catch (ex: ResolutionException) {
            log.error("can't read OpenAPI description!")
            log.error(ex.message)
            throw ParserException(ex)
        } catch (ex: ValidationException) {
            log.error("failed to parse OpenAPI description!")
            log.error(ex.results().toString())
            throw ParserException(ex)
        }
    }

    private fun run(apiPath: String): ParserOpenApi {
        val api = OpenApi3Parser()
            .parse(toURL(apiPath), true)

        val results = OpenApi3Validator
            .instance()
            .validate(api)

        return OpenApi(api, results)
    }

}
