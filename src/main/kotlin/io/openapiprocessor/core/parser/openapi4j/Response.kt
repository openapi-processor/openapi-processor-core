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

package io.openapiprocessor.core.parser.openapi4j

import io.openapiprocessor.core.parser.MediaType as ParserMediaType
import io.openapiprocessor.core.parser.Response as ParserResponse
import org.openapi4j.parser.model.v3.MediaType as O4jMediaType
import org.openapi4j.parser.model.v3.Response as O4jResponse

/**
 * openapi4j Response abstraction.
 *
 * @author Martin Hauner
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
