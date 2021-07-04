/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
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
