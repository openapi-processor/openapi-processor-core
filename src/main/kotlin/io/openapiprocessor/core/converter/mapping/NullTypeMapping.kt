/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping

/**
 * Used with [io.openapiprocessor.core.converter.ApiOptions.typeMappings] to map an OpenAPI nullable
 * property to a wrapper type of the plain type. E.g. org.openapitools.jackson.nullable.JsonNullable
 */
class NullTypeMapping(

    /**
     * The OpenAPI schema type that should be mapped to the {@link #targetTypeName} java type.
     */
    val sourceTypeName: String = "null",

    /**
     * The fully qualified java type name that will be used as the result type.
     */
    val targetTypeName: String,

    /**
     * An "undefined" value that should be used as the initial value of the property. e.g.
     * JonNullable.undefined()
     */
    val undefined: String? = null

): Mapping, TargetTypeMapping  {

    /**
     * Returns the target type of this type mapping.
     *
     * @return the target type
     */
    override fun getTargetType (): TargetType {
        return TargetType(targetTypeName, emptyList())
    }

    override fun getChildMappings(): List<Mapping> {
        return listOf(this)
    }

}
