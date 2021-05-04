/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser

/**
 * OpenAPI MediaType abstraction.
 */
interface MediaType {
    fun getSchema(): Schema
    val encoding: Map<String, Encoding>
}
