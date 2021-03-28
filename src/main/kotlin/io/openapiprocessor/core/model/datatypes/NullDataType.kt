/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.model.datatypes

/**
 * Null(able) data type wrapper. Assumes a single generic parameter.
 */
class NullDataType(
    private val type: String,
    private val pkg: String,
    val init: String?,
    private val dataType: DataType
): DataType {

    override fun getName(): String {
        return "$type<${dataType.getName()}>"
    }

    override fun getPackageName(): String {
        return pkg
    }

    override fun getImports(): Set<String> {
        return setOf("${getPackageName()}.$type") + dataType.getImports()
    }

}
