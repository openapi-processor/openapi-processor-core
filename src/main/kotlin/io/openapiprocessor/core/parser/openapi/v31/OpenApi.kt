/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser.openapi.v31

import io.openapiparser.model.v31.OpenApi as OpenApi31
import io.openapiparser.model.v31.PathItem as PathItem31
import io.openapiprocessor.core.parser.Path
import io.openapiprocessor.core.parser.RefResolver as ParserRefResolver
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import io.openapiprocessor.core.parser.OpenApi as ParserOpenApi
import io.openapiprocessor.core.parser.openapi.v31.Path as Path31

class OpenApi(
    private val api: OpenApi31
) : ParserOpenApi {
    private val log: Logger = LoggerFactory.getLogger(this.javaClass.name)

    override fun getPaths(): Map<String, Path> {
        val paths = linkedMapOf<String, Path>()

        api.paths?.pathItems?.forEach { (name: String, value: PathItem31) ->
            var path = value
            if (path.isRef) {
                path = path.refObject
            }
            paths[name] = Path31(name, path)
        }

        return paths
    }

    override fun getRefResolver(): ParserRefResolver = RefResolver(api)

    override fun printWarnings() {
    }

    override fun hasWarnings(): Boolean {
        return false
    }

}
