/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser

/**
 * OpenAPI Schema abstraction.
 */
interface Schema {

    /**
     * OpenAPI schema type, ie. array, string, etc.
     */
    fun getType(): String?
    fun getFormat(): String?

    // $ref
    fun getRef(): String?

    // array
    fun getItem(): Schema

    // object
    fun getProperties(): Map<String, Schema>

    // composed object
    fun getItems(): List<Schema>
    fun itemsOf(): String?

    // enum
    fun getEnum(): List<*>

    fun getDefault(): Any?

    // default false
    fun isDeprecated(): Boolean

    fun getRequired(): List<String>

    // default false
    fun getNullable(): Boolean
    fun getMinLength(): Int?
    fun getMaxLength(): Int?
    fun getMinItems(): Int?
    fun getMaxItems(): Int?
    fun getMaximum (): Number?

    // default false
    fun isExclusiveMaximum (): Boolean
    fun getMinimum (): Number?

    // default false
    fun isExclusiveMinimum (): Boolean

}
