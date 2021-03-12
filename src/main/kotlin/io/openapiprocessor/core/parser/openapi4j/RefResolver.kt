/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser.openapi4j

import io.openapiprocessor.core.parser.NamedSchema
import io.openapiprocessor.core.parser.RefResolver as ParserRefResolver
import io.openapiprocessor.core.parser.Schema as ParserSchema
import org.openapi4j.parser.model.v3.OpenApi3 as O4jOpenApi
import org.openapi4j.parser.model.v3.Schema as O4jSchema

/**
 * openapi4j $ref resolver.
 */
class RefResolver(private val api: O4jOpenApi): ParserRefResolver {

    override fun resolve(ref: ParserSchema): NamedSchema {
        val resolved: O4jSchema

        val refName = getRefName(ref.getRef()!!)
        val o4jSchema: O4jSchema = (ref as Schema).schema
        resolved = o4jSchema.getReference(api.context).getMappedContent(O4jSchema::class.java)

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
