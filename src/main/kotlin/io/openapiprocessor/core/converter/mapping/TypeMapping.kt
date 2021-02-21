/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping

/**
 * Used with [io.openapiprocessor.core.converter.ApiOptions.typeMappings] to map OpenAPI schemas
 * to java types.
 *
 * To override the type mapping of the OpenAPI `array` from a simple java array to another
 * collection type the [sourceTypeName] should be set to `array`.
 */
class TypeMapping (

    /**
     * The OpenAPI schema type that should be mapped to the {@link #targetTypeName} java type.
     */
    val sourceTypeName: String?,

    /**
     * The OpenAPI format of {@link #sourceTypeName} that should be mapped to the
     * {@link #targetTypeName} java type.
     */
    val sourceTypeFormat: String?,

    /**
     * The fully qualified java type name that will replace {@link #sourceTypeName}.
     */
    val targetTypeName: String,

    /**
     * The fully qualified java type names of all generic parameters to {@link #targetTypeName}.
     */
    val genericTypeNames: List<String> = emptyList()

): Mapping, TargetTypeMapping {

    constructor(sourceTypeName: String?, targetTypeName: String):
            this (sourceTypeName, null, targetTypeName, emptyList())

    constructor(sourceTypeName: String?, sourceTypeFormat: String?, targetTypeName: String):
            this (sourceTypeName, sourceTypeFormat, targetTypeName, emptyList())

    constructor(sourceTypeName: String?, targetTypeName: String, genericTypeNames: List<String>):
            this (sourceTypeName, null, targetTypeName, genericTypeNames)

    /**
     * Returns the full source type as {@link #sourceTypeName} and {@link #sourceTypeFormat} joined
     * by a ':' separator.
     *
     * @return the full source type
     */
    /*
    @Deprecated("do not use in new code", ReplaceWith("no replacement"))
    fun getFullSourceType(): String {
        return sourceTypeName + (sourceTypeFormat ? ":$sourceTypeFormat" : "")
    }
    */

    /**
     * Returns the target type of this type mapping.
     *
     * @return the target type
     */
    override fun getTargetType (): TargetType {
        return TargetType(targetTypeName, genericTypeNames)
    }

    override fun getChildMappings(): List<Mapping> {
        return listOf(this)
    }

}

fun List<Mapping>.toTypeMapping(): List<TypeMapping> {
    return map { it as TypeMapping }
}
