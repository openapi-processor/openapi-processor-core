/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.model.datatypes

/**
 * OpenAPI "allOf" composed schema type.
 */
class AllOfObjectDataType(
    private val name: DataTypeName,
    private val pkg: String,
    private val items: List<DataType> = emptyList(),
    override val constraints: DataTypeConstraints? = null,
    override val deprecated: Boolean = false
): DataType, ModelDataType {

    override fun getName(): String {
        return name.id
    }

    override fun getTypeName(): String {
        return name.type
    }

    override fun getPackageName(): String {
        return pkg
    }

    override fun getImports(): Set<String> {
        return setOf(getPackageName() + "." + getTypeName())
    }

    override val referencedImports: Set<String>
        get() {
            return properties.values
                .flatMap { it.getImports() }
                .toSet()
        }

    override fun isRequired(prop: String): Boolean {
        return constraints?.isRequired(prop) ?: false
    }

    override fun forEach(action: (property: String, dataType: DataType) -> Unit) {
        items.filterIsInstance<ObjectDataType>()
            .forEach {
                it.forEach(action)
            }
    }

    val properties: LinkedHashMap<String, DataType>
        get() {
            val properties = linkedMapOf<String, DataType>()

            items.filterIsInstance<ObjectDataType>()
                .forEach {
                    properties.putAll(it.getProperties())
                }

            return properties
        }

}
