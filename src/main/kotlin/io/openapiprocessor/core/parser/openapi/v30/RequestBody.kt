/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser.openapi.v30

import io.openapiparser.model.v30.MediaType as MediaType30
import io.openapiparser.model.v30.RequestBody as RequestBody30
import io.openapiprocessor.core.parser.MediaType as ParserMediaType
import io.openapiprocessor.core.parser.RequestBody as ParserRequestBody

/**
 * openapi-parser RequestBody abstraction.
 */
class RequestBody(private val requestBody: RequestBody30): ParserRequestBody {

    override fun getRequired(): Boolean {
        return requestBody.required ?: false
    }

    override fun getContent(): Map<String, ParserMediaType> {
        val content = linkedMapOf<String, ParserMediaType>()
        requestBody.content.forEach { (key: String, entry: MediaType30) ->
            content[key] = MediaType(entry)
        }
        return content
    }

}
