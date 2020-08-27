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

package io.openapiprocessor.core.model

import io.openapiprocessor.core.model.datatypes.DataType
import io.openapiprocessor.core.model.datatypes.MappedDataType
import io.openapiprocessor.core.model.datatypes.ObjectDataType
import io.openapiprocessor.core.model.datatypes.StringEnumDataType

/**
 * Container of data types from OpenAPI '#/component/schemas'.
 *
 * @author Martin Hauner
 */
class DataTypes {

    private val types: MutableMap<String, DataType> = mutableMapOf()
    private val mappedTypes: MutableMap<String, MappedDataType> = mutableMapOf()

    /**
     * provides all named data types (including simple data types) used by the api endpoint.
     *
     * @return list of data types
     */
    fun getDataTypes(): Collection<DataType> {
        return types.values
    }

    /**
     * provides the *object* data types (model classes) used by the api endpoints.
     * For this objects the processor will create POJOs classes.
     *
     * @return list of object data types
     */
    fun getObjectDataTypes(): Collection<ObjectDataType> {
        return types.values
            .filterIsInstance<ObjectDataType>()
            .toList()
    }

    /**
     * provides the enum data types (model classes) used by the api endpoints.
     * For this objects the processor will create enum classes.
     *
     * @return list of enum data types
     */
    fun getEnumDataTypes(): Collection<StringEnumDataType> {
        return types.values
            .filterIsInstance<StringEnumDataType>()
            .toList()
    }

    fun add(dataTypes: List<DataType>) {
        dataTypes.forEach {
            types[it.getName()] = it
        }
    }

    /**
     * remember a data type.
     *
     * @param dataType the source data type
     */
    fun add(dataType: DataType) {
        add (dataType.getName(), dataType)
    }

    /**
     * remember a data type.
     *
     * @param name name of the data type
     * @param dataType the source data type
     */
    fun add(name: String, dataType: DataType) {
        if (dataType is MappedDataType) {
            mappedTypes[name] = dataType
        } else {
            types[name] = dataType
        }
    }

    /**
     * find data type by name.
     *
     * @param name the name
     * @return the data type or null if not found
     */
    fun find (name: String): DataType {
        return types[name] ?: mappedTypes[name]!!
    }

}
