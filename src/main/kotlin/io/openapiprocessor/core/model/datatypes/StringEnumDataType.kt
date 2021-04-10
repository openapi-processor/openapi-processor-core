/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.model.datatypes

/**
 * OpenAPI type 'string' with enum constraint maps to enum class.
 */
class StringEnumDataType(
    private val type: String,
    private val pkg: String,
    val values: List<String> = emptyList(),
    override val constraints: DataTypeConstraints? = null,
    override val deprecated: Boolean = false
): DataType {

    override fun getName(): String {
        return type
    }

    override fun getPackageName(): String {
        return pkg
    }

    override fun getImports(): Set<String> {
        return setOf("${getPackageName()}.${getName()}")
    }

}


