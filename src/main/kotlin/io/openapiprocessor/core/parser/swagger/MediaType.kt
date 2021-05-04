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

package io.openapiprocessor.core.parser.swagger

import io.openapiprocessor.core.parser.Encoding
import io.openapiprocessor.core.parser.MediaType as ParserMediaType
import io.swagger.v3.oas.models.media.MediaType as SwaggerMediaType

/**
 * Swagger MediaType abstraction.
 *
 * @author Martin Hauner
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
