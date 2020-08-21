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

import io.openapiprocessor.core.parser.RefResolver as ParserRefResolver
import io.openapiprocessor.core.parser.Schema as ParserSchema
import io.swagger.v3.oas.models.Components as SwaggerComponents
import io.swagger.v3.oas.models.media.Schema as SwaggerSchema

/**
 * Swagger $ref resolver.
 *
 * @author Martin Hauner
 */
class RefResolver(private val components: SwaggerComponents?): ParserRefResolver {

    override fun resolve(ref: ParserSchema): ParserSchema {
        val refName = getRefName(ref.getRef()!!)

        val schema: SwaggerSchema<*>? = components?.schemas?.get(refName)
        if (schema == null) {
            throw Exception("failed to resolve ${ref.getRef()}")
        }

        return Schema(schema)
    }

    private fun getRefName(ref: String): String {
        return ref.substring(ref.lastIndexOf('/') + 1)
    }

}
