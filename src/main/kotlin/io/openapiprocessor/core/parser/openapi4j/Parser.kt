/*
 * Copyright 2020 the original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
 *
 * @author Martin Hauner
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
