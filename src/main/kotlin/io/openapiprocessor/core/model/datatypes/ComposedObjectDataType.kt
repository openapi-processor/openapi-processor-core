/*
 * Copyright 2020 the original authors
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
 * OpenAPI composed schema type.
 *
 * @author Martin Hauner
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
            .map { it.getImports() }
            .flatten()
            .toSet()
    }

    // todo find better name
    override fun isComposed(): Boolean {
        return of != "allOf"
    }

    override fun isModel(): Boolean {
        return of == "allOf"
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

}
