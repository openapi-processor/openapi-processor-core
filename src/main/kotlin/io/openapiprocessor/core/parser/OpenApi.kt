/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser

/**
 * OpenAPI parser result abstraction.
 */
interface OpenApi {

    fun getPaths(): Map<String, Path>

    fun getRefResolver(): RefResolver

    fun printWarnings()
    fun hasWarnings(): Boolean

}
