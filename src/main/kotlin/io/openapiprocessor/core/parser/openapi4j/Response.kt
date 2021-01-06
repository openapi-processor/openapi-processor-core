/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser.openapi4j

import io.openapiprocessor.core.parser.MediaType as ParserMediaType
import io.openapiprocessor.core.parser.Response as ParserResponse
import org.openapi4j.parser.model.v3.MediaType as O4jMediaType
import org.openapi4j.parser.model.v3.Response as O4jResponse

/**
 * openapi4j Response abstraction.
 */
class Response(private val response: O4jResponse): ParserResponse {

    override fun getContent(): Map<String, ParserMediaType> {
        val content = linkedMapOf<String, ParserMediaType>()
        response.contentMediaTypes?.forEach { (key: String, entry: O4jMediaType) ->
            content[key] = MediaType(entry)
        }
        return content
    }

    override val description: String?
        get() = response.description

}
