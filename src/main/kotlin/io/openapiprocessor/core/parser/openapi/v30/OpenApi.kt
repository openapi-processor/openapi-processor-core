/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser.openapi.v30

import io.openapiprocessor.core.parser.Path
import io.openapiprocessor.core.parser.RefResolver
import io.openapiprocessor.core.parser.OpenApi as ParserOpenApi

class OpenApi : ParserOpenApi {

    override fun getPaths(): Map<String, Path> {
        TODO("Not yet implemented")
    }

    override fun getRefResolver(): RefResolver {
        TODO("Not yet implemented")
    }

    override fun printWarnings() {
        TODO("Not yet implemented")
    }

}
