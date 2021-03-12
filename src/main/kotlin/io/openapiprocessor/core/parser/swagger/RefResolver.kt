/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser.swagger

import io.openapiprocessor.core.parser.NamedSchema as ParserNamedSchema
import io.openapiprocessor.core.parser.RefResolver as ParserRefResolver
import io.openapiprocessor.core.parser.Schema as ParserSchema
import io.swagger.v3.oas.models.media.Schema as SwaggerSchema
import io.swagger.v3.oas.models.OpenAPI

/**
 * Swagger $ref resolver.
 */
class RefResolver(private val openapi: OpenAPI): ParserRefResolver {

    override fun resolve(ref: ParserSchema): ParserNamedSchema {
        val refName = getRefName(ref.getRef()!!)

        val schema: SwaggerSchema<*>? = openapi.components?.schemas?.get(refName)
        if (schema == null) {
            throw Exception("failed to resolve ${ref.getRef()}")
        }

        return ParserNamedSchema(refName, Schema(schema))
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
