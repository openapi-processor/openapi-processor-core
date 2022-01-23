/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser.openapi.v31

import io.openapiparser.model.v31.OpenApi as OpenApi31
import io.openapiprocessor.core.parser.Path
import io.openapiprocessor.core.parser.RefResolver
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import io.openapiprocessor.core.parser.OpenApi as ParserOpenApi

class OpenApi(
    private val api: OpenApi31
) : ParserOpenApi {
    private val log: Logger = LoggerFactory.getLogger(this.javaClass.name)

    override fun getPaths(): Map<String, Path> {
        TODO("Not yet implemented")
    }

    override fun getRefResolver(): RefResolver {
        TODO("Not yet implemented")
    }

    override fun printWarnings() {
        TODO("Not yet implemented")
    }

    override fun hasWarnings(): Boolean {
        TODO("Not yet implemented")
    }

}
