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
 *
 * @author Martin Hauner
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
