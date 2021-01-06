/*
 * Copyright © 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser

/**
 * OpenAPI Parameter abstraction.
 */
interface Parameter {

    fun getIn(): String
    fun getName(): String

    fun getSchema(): Schema
    fun isRequired(): Boolean
    fun isDeprecated(): Boolean

    val description: String?

}

// todo properties
