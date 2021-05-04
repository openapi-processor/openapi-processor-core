/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser.swagger

import io.openapiprocessor.core.parser.Encoding
import io.openapiprocessor.core.parser.MediaType as ParserMediaType
import io.swagger.v3.oas.models.media.MediaType as SwaggerMediaType

/**
 * Swagger MediaType abstraction.
 */
class MediaType(val mediaType: SwaggerMediaType): ParserMediaType {
    override fun getSchema() = Schema(mediaType.schema)

    override val encoding: Map<String, Encoding>
        get() {
            val encoding = mutableMapOf<String, Encoding>()
            mediaType.encoding?.forEach {
                encoding[it.key] = Encoding(it.value.contentType)
            }
            return encoding
        }
}
