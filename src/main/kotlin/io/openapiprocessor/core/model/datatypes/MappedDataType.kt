/*
 * Copyright 2019 the original authors
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
 * OpenAPI schema mapped to a java type.
 *
 * @author Martin Hauner
 */
open class MappedDataType(

    private val type: String,
    private val pkg: String,
    /*private*/ val genericTypes: List<String> = emptyList(),
    constraints: DataTypeConstraints? = null,
    deprecated: Boolean = false,
    simpleDataType: Boolean = false

): DataTypeBase(constraints, deprecated) {

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
