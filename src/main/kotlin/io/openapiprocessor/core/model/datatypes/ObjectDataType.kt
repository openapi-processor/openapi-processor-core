/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.model.datatypes

import io.openapiprocessor.core.model.Documentation

/**
 * OpenAPI named object schemas type or an inline object schema.
 */
open class ObjectDataType(
    private val name: DataTypeName,
    private val pkg: String,
    /** linked map to preserve order */
    private val properties: LinkedHashMap<String, PropertyDataType> = linkedMapOf(),
    override val constraints: DataTypeConstraints? = null,
    override val deprecated: Boolean = false,
    override val documentation: Documentation? = null
): ModelDataType {
    override var implementsDataType: InterfaceDataType? = null

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
        return setOf("${getPackageName()}.${getTypeName()}")
    }

    override val referencedImports: Set<String>
        get() {
            return propertiesImports + implementsImports
        }

    fun addObjectProperty(name: String, type: PropertyDataType) {
        properties[name] = type
    }

    fun getObjectProperty(name: String): PropertyDataType {
        return properties[name]!!
    }

    fun getProperties(): Map<String, PropertyDataType> {
        return properties
    }

    override fun isRequired(prop: String): Boolean {
        return constraints?.isRequired(prop) ?: false
    }

    override fun forEach(action: (property: String, dataType: DataType) -> Unit) {
        for (p in properties) action(p.key, p.value)
    }

    private val propertiesImports: Set<String>
        get() {
            return properties.values
                .map { it.getImports() }
                .flatten()
                .toSet()
        }

    private val implementsImports: Set<String>
        get() {
            return implementsDataType?.getImports() ?: emptySet()
        }
}
