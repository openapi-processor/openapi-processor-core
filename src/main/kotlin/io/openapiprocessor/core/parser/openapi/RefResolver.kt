/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser.openapi

import io.openapiprocessor.core.parser.NamedSchema
import io.openapiprocessor.core.parser.openapi.v30.Schema
import io.openapiprocessor.core.parser.RefResolver as ParserRefResolver
import io.openapiprocessor.core.parser.Schema as ParserSchema
import io.openapiparser.model.v30.OpenApi as OpenApi30
import io.openapiparser.model.v30.Schema as Schema30

/**
 * openapi-parser $ref resolver.
 */
class RefResolver(private val api: OpenApi30): ParserRefResolver {

    override fun resolve(ref: ParserSchema): NamedSchema {
        val schema: Schema30 = (ref as Schema).schema
        return NamedSchema(getRefName(ref.getRef()!!), Schema(schema.refObject))
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
