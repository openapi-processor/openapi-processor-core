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

import io.openapiprocessor.core.parser.ParserException
import io.openapiprocessor.core.support.toURL
import io.swagger.v3.parser.OpenAPIV3Parser
import io.swagger.v3.parser.core.models.ParseOptions
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import io.openapiprocessor.core.parser.OpenApi as ParserOpenApi

const val SCHEME_RESOURCE = "resource:"

/**
 * swagger parser.
 *
 * @author Martin Hauner
 */
open class Parser {
    private val log: Logger = LoggerFactory.getLogger(this.javaClass.name)

    private enum class Source {URL, STRING}

    fun parse(apiPath: String): ParserOpenApi {
        try {
            return run(apiPath, Source.URL)
        } catch (ex: Exception) {
            log.error("can't read OpenAPI description!")
            throw ParserException(ex)
        }
    }

    /** test only */
    fun parseString(api: String): ParserOpenApi {
        try {
            return run(api, Source.STRING)
        } catch (ex: Exception) {
            throw ParserException(ex)
        }
    }

    private fun run(api: String, source: Source): ParserOpenApi {
        val opts = ParseOptions()
        // loads $refs to other files into #/components/schema and replaces the $refs to the
        // external files with $refs to #/components/schema.
        opts.isResolve = true

        val result = when(source) {
            Source.URL -> {
                OpenAPIV3Parser().readLocation(preparePath(api), null, opts)
            }
            Source.STRING -> {
                OpenAPIV3Parser().readContents(api, null, opts)
            }
        }

        if (result.openAPI == null) {
            result.messages?.forEach {
                log.error(it)
            }
            throw FailedException()
        }

        return OpenApi(result)
    }

    private fun preparePath(path: String): String {
        // the swagger parser only works with http(s) & file protocols.

        // If it is something different (or nothing) it tries to find the given path as-is on the
        // file system. If that fails it tries to load the path as resource.

        if (isResource(path)) {
            // strip resource: (only used by tests) to load test files from the resources
            return path.substring(SCHEME_RESOURCE.length)
        }

        return toURL(path).toString ()
    }

    private fun isResource(path: String): Boolean {
        return path.startsWith(SCHEME_RESOURCE)
    }

}
