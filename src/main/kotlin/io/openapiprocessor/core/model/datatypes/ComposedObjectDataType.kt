/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.model.datatypes

/**
 * OpenAPI "allOf" composed schema type.
 */
class ComposedObjectDataType(

    private val type: String,
    private val pkg: String,
    private val of: String,
    private val items: List<DataType> = emptyList(),
    constraints: DataTypeConstraints? = null,
    deprecated: Boolean = false

): DataTypeBase(constraints, deprecated), ModelDataType {

    override fun getName(): String {
        return type
    }

    override fun getPackageName(): String {
        return pkg
    }

    override fun getImports(): Set<String> {
        return setOf(getPackageName() + "." + getName())
    }

    override fun getReferencedImports(): Set<String> {
        return items
            .filterIsInstance<ObjectDataType>()
            .map { it.getImports() }
            .flatten()
            .toSet()
    }

    override fun getProperties(): Map<String, DataType> {
        val properties = linkedMapOf<String, DataType>()

        if (of == "allOf") {
            items.forEach {
                if (it is ObjectDataType) {
                    properties.putAll(it.getProperties())
                }
            }
        }

        return properties
    }

    override fun isRequired(prop: String): Boolean {
        return getConstraints()?.isRequired(prop) ?: false
    }

    override fun forEach(action: (property: String, dataType: DataType) -> Unit) {
        for (p in getProperties()) action(p.key, p.value)
    }

}
