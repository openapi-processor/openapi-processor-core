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
 * Result data type wrapper. Assumes a single generic parameter.
 *
 * @author Martin Hauner
 */
class ResultDataType(

    private val type: String,
    private val pkg: String,
    private val dataType: DataType

): DataType {

    // todo multi check inside?
    override fun getName(): String {
        return "$type<${dataType.getName()}>"
    }

    override fun getPackageName(): String {
        return pkg
    }

    override fun getImports(): Set<String> {
        return setOf("${getPackageName()}.$type") + dataType.getImports()
    }

    override fun getReferencedImports(): Set<String> {
        return emptySet()
    }

    override fun isComposed(): Boolean {
        return dataType.isComposed()
    }

    /**
     * type if the result data type can have multiple values.
     *
     * @return type with ? as the generic parameter
     */
    fun getNameMulti(): String {
        return "$type<?>"
    }

    fun getImportsMulti(): Set<String> {
        return setOf("${getPackageName()}.$type")
    }

}
