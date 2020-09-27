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

package io.openapiprocessor.core.parser

import io.openapiprocessor.core.parser.swagger.Parser as Swagger
import io.openapiprocessor.core.parser.openapi4j.Parser as OpenApi4J
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * OpenAPI parser abstraction. Supports swagger or openapi4 parser.
 *
 * @author Martin Hauner
 */
class Parser {
    private val log: Logger = LoggerFactory.getLogger(this.javaClass.name)

    fun parse(processorOptions: Map<String, *>): OpenApi {
        val apiPath: String = processorOptions["apiPath"].toString()

        when(val parser= processorOptions["parser"]?.toString()) {
            ParserType.SWAGGER.name -> {
                log.info("using SWAGGER parser")
                return Swagger().parse(apiPath)
            }
            ParserType.OPENAPI4J.name -> {
                log.info("using OPENAPI4J parser")
                return OpenApi4J().parse(apiPath)
            }
            else -> {
                if (parser != null) {
                    log.warn("unknown parser type: {}", parser)
                    log.warn("available parsers: SWAGGER, OPENAPI4J")
                }
                return Swagger().parse(apiPath)
            }
        }
    }
}
