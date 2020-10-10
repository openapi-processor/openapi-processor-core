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

import io.openapiprocessor.core.misc.toURL
import io.swagger.v3.parser.OpenAPIV3Parser
import io.swagger.v3.parser.core.models.ParseOptions
import io.swagger.v3.parser.core.models.SwaggerParseResult
import io.openapiprocessor.core.parser.OpenApi as ParserOpenApi

const val SCHEME_RESOURCE = "resource:"

/**
 * swagger parser.
 *
 * @author Martin Hauner
 */
open class Parser {

    fun parse(apiPath: String): ParserOpenApi {
        val opts = ParseOptions()
        // loads $refs to other files into #/components/schema and replaces the $refs to the
        // external files with $refs to #/components/schema.
        opts.isResolve = true

        val result: SwaggerParseResult = OpenAPIV3Parser()
                  .readLocation(preparePath(apiPath), null, opts)

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
