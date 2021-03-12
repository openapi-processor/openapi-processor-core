/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser

/**
 * Resolves $ref objects from an OpenAPI.
 */
interface RefResolver {
    fun resolve(ref: Schema): NamedSchema
}

class NamedSchema(val name: String?, val schema: Schema) {
    val hasName = name != null
    val hasNoName = name == null
}
