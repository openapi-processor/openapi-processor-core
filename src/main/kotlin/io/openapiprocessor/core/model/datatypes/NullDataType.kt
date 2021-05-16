/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.model.datatypes

/**
 * Null(able) data type wrapper. Assumes a single generic parameter.
 */
class NullDataType(
    private val name: String,
    private val pkg: String,
    private val dataType: DataType,
    val init: String? = null
): DataType {

    override fun getName(): String {
        return "$name<${dataType.getName()}>"
    }

    override fun getTypeName(): String {
        return "$name<${dataType.getTypeName()}>"
    }

    override fun getPackageName(): String {
        return pkg
    }

    override fun getImports(): Set<String> {
        return setOf("${getPackageName()}.$name") + dataType.getImports()
    }

}
