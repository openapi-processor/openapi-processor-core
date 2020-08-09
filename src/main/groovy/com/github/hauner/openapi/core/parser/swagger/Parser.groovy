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

package com.github.hauner.openapi.core.parser.swagger

import com.github.hauner.openapi.core.parser.OpenApi as ParserOpenApi
import io.swagger.v3.parser.OpenAPIV3Parser
import io.swagger.v3.parser.core.models.ParseOptions
import io.swagger.v3.parser.core.models.SwaggerParseResult

import static com.github.hauner.openapi.core.misc.URL.toURL

/**
 * swagger parser.
 *
 * @author Martin Hauner
 */
class Parser {
    public static final String SCHEME_RESOURCE = "resource:"

    ParserOpenApi parse (String apiPath) {
        ParseOptions opts = new ParseOptions(
            // loads $refs to other files into #/components/schema and replaces the $refs to the
            // external files with $refs to #/components/schema.
            resolve: true)

        SwaggerParseResult result = new OpenAPIV3Parser ()
                  .readLocation (preparePath (apiPath), null, opts)

        new OpenApi(result)
    }

    private static String preparePath (String path) {
        // the swagger parser only works with http(s) & file protocols.

        // If it is something different (or nothing) it tries to find the given path as-is on the
        // file system. If that fails it tries to load the path as resource.

        if (isResource (path)) {
            // strip resource: (only used by tests) to load test files from the resources
            return path.substring (SCHEME_RESOURCE.size ())
        }

        toURL (path).toString ()
    }

    static boolean isResource (String path) {
        path.startsWith (SCHEME_RESOURCE)
    }

}
