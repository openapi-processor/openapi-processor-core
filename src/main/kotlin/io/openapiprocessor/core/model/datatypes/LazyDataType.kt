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

import io.openapiprocessor.core.converter.SchemaInfo
import io.openapiprocessor.core.model.DataTypes

/**
 * OpenAPI $ref type that is lazily evaluated. It is used to break loops in the schema definitions.
 *
 * @author Martin Hauner
 */
class LazyDataType(
    private val info: SchemaInfo,
    private val dataTypes: DataTypes
): DataTypeBase() {

    override fun getName(): String {
        return getDataType().getName()
    }

    override fun getPackageName(): String {
        return  getDataType().getPackageName()
    }

    override fun getImports(): Set<String> {
        return getDataType().getImports()
    }

    override fun getReferencedImports(): Set<String> {
        return getDataType().getReferencedImports()
    }

    private fun getDataType(): DataType {
        return dataTypes.find (info.getName())
    }

}
