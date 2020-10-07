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

import io.openapiprocessor.core.parser.OpenApi as ParserOpenApi
import io.openapiprocessor.core.parser.Path as ParserPath
import io.openapiprocessor.core.parser.RefResolver as ParserRefResolver
import org.openapi4j.core.validation.ValidationResults
import org.openapi4j.parser.model.v3.OpenApi3 as O4jOpenApi
import org.openapi4j.parser.model.v3.Path as O4jPath

/**
 * openapi4j parser result.
 *
 * @author Martin Hauner
 */
class OpenApi(
    private val api: O4jOpenApi,
    private val validations: ValidationResults,
): ParserOpenApi {
    private val refResolver: RefResolverNative = RefResolverNative(api)

    override fun getPaths(): Map<String, ParserPath> {
        val paths = linkedMapOf<String, ParserPath>()

        api.paths.forEach { (name: String, value: O4jPath) ->
            var path = value
            if (path.isRef) {
                path = refResolver.resolve(path)
            }

            paths[name] = Path(name, path, refResolver)
        }

        return paths
    }

    override fun getRefResolver(): ParserRefResolver = RefResolver (api)

    override fun printWarnings() {
    }

}
