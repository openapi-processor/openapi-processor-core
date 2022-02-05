/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter

import io.openapiprocessor.core.parser.RefResolver
import io.openapiprocessor.core.parser.Schema

class SchemaInfoAllOf(
    endpoint: Endpoint,
    name: String,
    contentType: String = "",
    schema: Schema?,
    resolver: RefResolver
) : SchemaInfo(endpoint, name, contentType, schema, resolver) {

    override fun getType(): String {
       return "object"
    }

}
