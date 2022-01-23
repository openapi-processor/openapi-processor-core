/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser.openapi.v30

import io.openapiprocessor.core.parser.Path
import io.openapiprocessor.core.parser.openapi.RefResolver
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import io.openapiparser.model.v30.OpenApi as OpenApi30
import io.openapiparser.model.v30.PathItem as PathItem30
import io.openapiprocessor.core.parser.OpenApi as ParserOpenApi
import io.openapiprocessor.core.parser.RefResolver as ParserRefResolver
import io.openapiprocessor.core.parser.openapi.v30.Path as Path30


/**
 * openapi-parser result.
 */
open class OpenApi(
    private val api: OpenApi30
): ParserOpenApi {
    private val log: Logger = LoggerFactory.getLogger(this.javaClass.name)

    override fun getPaths(): Map<String, Path> {
        val paths = linkedMapOf<String, Path>()

        api.paths.pathItems.forEach { (name: String, value: PathItem30) ->
            var path = value
            if (path.isRef) {
                path = path.refObject
            }
            paths[name] = Path30(name, value)
        }

        return paths
    }

    override fun getRefResolver(): ParserRefResolver = RefResolver(api)

    override fun printWarnings() {
    }

    override fun hasWarnings(): Boolean {
        return false;
    }

}

