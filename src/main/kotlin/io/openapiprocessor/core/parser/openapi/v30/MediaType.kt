/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser.openapi.v30

import io.openapiprocessor.core.parser.Encoding
import io.openapiparser.model.v30.MediaType as MediaType30
import io.openapiprocessor.core.parser.MediaType as ParserMediaType

/**
 * openapi-parser MediaType abstraction.
 */
class MediaType(val mediaType: MediaType30): ParserMediaType {
    override fun getSchema() = Schema(mediaType.schema!!)  // todo !!

    override val encodings: Map<String, Encoding>
        get() {
            val encoding = mutableMapOf<String, Encoding>()
            mediaType.encoding.forEach {
                encoding[it.key] = Encoding(it.value.contentType)
            }
            return encoding
        }
}
