/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping

/**
 * Used with [io.openapiprocessor.core.converter.ApiOptions.typeMappings] to map an OpenAPI response
 * to a plain java type or to a wrapper type of the plain type.
 */
class ResultTypeMapping(

    /**
     * The fully qualified java type name that will be used as the result type.
     */
    val targetTypeName: String

): Mapping, TargetTypeMapping {

    override fun getChildMappings(): List<Mapping> {
        return listOf(this)
    }

    /**
     * Returns the target type of this type mapping.
     *
     * @return the target type
     */
    override fun getTargetType(): TargetType {
        return TargetType(targetTypeName, emptyList())
    }

}
