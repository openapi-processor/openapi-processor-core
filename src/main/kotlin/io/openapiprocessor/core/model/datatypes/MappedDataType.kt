/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.model.datatypes

/**
 * OpenAPI schema mapped to a java type.
 */
open class MappedDataType(

    private val type: String,
    private val pkg: String,
    /*private*/ val genericTypes: List<String> = emptyList(),
    constraints: DataTypeConstraints? = null,
    override val deprecated: Boolean = false,
    val simpleDataType: Boolean = false

): DataTypeBase(constraints) {

    override fun getName(): String {
        return if (genericTypes.isEmpty()) {
            type
        } else {
            "${type}<${getGenericTypeNames().joinToString(", ")}>"
        }
    }

    override fun getPackageName(): String {
        return pkg
    }

    override fun getImports(): Set<String> {
        return setOf("${getPackageName()}.$type") + genericTypes.filter { it != "?" }
    }

    private fun getGenericTypeNames(): List<String> {
        return genericTypes.map {
            getClassName (it)
        }
    }

    private fun getClassName(ref: String): String {
        return ref.substring(ref.lastIndexOf('.') + 1)
    }

}
