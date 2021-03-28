/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.model.datatypes

/**
 * OpenAPI "oneOf/anyOf" composed schema type.
 */
class AnyOneOfObjectDataType(
    private val type: String,
    private val pkg: String,
    private val of: String,
    private val items: List<DataType> = emptyList(),
    override val constraints: DataTypeConstraints? = null,
    override val deprecated: Boolean = false
): DataTypeBase() {

    override fun getName(): String {
        return type
    }

    override fun getPackageName(): String {
        return pkg
    }

    fun forEach(action: (dataType: DataType) -> Unit) {
        for (i in items) action(i)
    }

}
