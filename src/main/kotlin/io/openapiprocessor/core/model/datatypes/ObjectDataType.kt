/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.model.datatypes

import io.openapiprocessor.core.model.Documentation

/**
 * OpenAPI named object schemas type or an inline object schema.
 */
class ObjectDataType(

    private val type: String,
    private val pkg: String,
    private val properties: LinkedHashMap<String, DataType> = linkedMapOf(), // preserves order
    constraints: DataTypeConstraints? = null,
    deprecated: Boolean = false,
    override val documentation: Documentation? = null

): DataTypeBase(constraints, deprecated, documentation), ModelDataType {

    override fun getName(): String {
        return type
    }

    override fun getPackageName(): String {
        return pkg
    }

    override fun getReferencedImports(): Set<String> {
        return properties.values
            .map { it.getImports() }
            .flatten()
            .toSet()
    }

    fun addObjectProperty(name: String, type: DataType) {
        properties[name] = type
    }

    fun getObjectProperty(name: String): DataType {
        return properties[name]!!
    }

    @Deprecated("do not override groovys getProperties()", ReplaceWith("getProperties()"))
    fun getObjectProperties(): Map<String, DataType> {
        return getProperties()
    }

    override fun getProperties(): Map<String, DataType> {
        return properties
    }

    override fun isRequired(prop: String): Boolean {
        return getConstraints()?.isRequired(prop) ?: false
    }

    override fun forEach(action: (property: String, dataType: DataType) -> Unit) {
        for (p in properties) action(p.key, p.value)
    }

}
