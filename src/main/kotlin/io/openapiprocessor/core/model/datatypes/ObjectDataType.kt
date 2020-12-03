/*
 * Copyright 2019-2020 the original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.openapiprocessor.core.model.datatypes

/**
 * OpenAPI named #/component/schemas type or an inline type.
 *
 * @author Martin Hauner
 * @author Bastian Wilhelm
 */
class ObjectDataType(

    private val type: String,
    private val pkg: String,
    private val properties: LinkedHashMap<String, DataType> = linkedMapOf(), // preserves order
    constraints: DataTypeConstraints? = null,
    deprecated: Boolean = false

): DataTypeBase(constraints, deprecated), ModelDataType {

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

    override fun isModel(): Boolean {
        return true
    }

    override fun getProperties(): Map<String, DataType> {
        return properties
    }

    override fun isRequired(prop: String): Boolean {
        return getConstraints()?.isRequired(prop) ?: false
    }

}
