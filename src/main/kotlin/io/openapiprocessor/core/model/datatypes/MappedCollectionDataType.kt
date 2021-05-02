/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.model.datatypes

/**
 * OpenAPI schema collection mapped to a java type. The java type is expected to have a single
 * generic parameter.
 */
open class MappedCollectionDataType(
    private val type: String,
    private val pkg: String,
    override val item: DataType,
    override val constraints: DataTypeConstraints? = null,
    override val deprecated: Boolean = false
): DataType, CollectionDataType {

    override fun getName(): String {
        return "${type}<${item.getName()}>"
    }

    fun getName(annotation: String): String {
        return "${type}<$annotation ${item.getName()}>"
    }

    override fun getPackageName(): String {
        return pkg
    }

    override fun getImports(): Set<String> {
        return setOf(getPackageName() + "." + type) + item.getImports()
    }

}
