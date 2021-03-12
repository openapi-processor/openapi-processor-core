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

import io.openapiprocessor.core.parser.NamedSchema
import io.openapiprocessor.core.parser.RefResolver as ParserRefResolver
import io.openapiprocessor.core.parser.Schema as ParserSchema
import org.openapi4j.parser.model.v3.OpenApi3 as O4jOpenApi
import org.openapi4j.parser.model.v3.Schema as O4jSchema

/**
 * openapi4j $ref resolver.
 *
 * @author Martin Hauner
 */
class RefResolver(private val api: O4jOpenApi): ParserRefResolver {

    override fun resolve(ref: ParserSchema): NamedSchema {
        val resolved: O4jSchema

        val refName = getRefName(ref.getRef()!!)
        val o4jCompSchema: O4jSchema? = api.components?.schemas?.get(refName)
        resolved = if (o4jCompSchema != null) {
            o4jCompSchema
        } else {
            val o4jSchema: O4jSchema = (ref as Schema).schema
            o4jSchema.getReference(api.context).getMappedContent(O4jSchema::class.java)
        }

        return NamedSchema (refName, Schema(resolved))
    }

    private fun getRefName(ref: String): String? {
        val split = ref.split('#')
        if (split.size > 1) {
            val hash = split[1]
            return hash.substring(hash.lastIndexOf('/') + 1)
        }
        return null
    }

}
