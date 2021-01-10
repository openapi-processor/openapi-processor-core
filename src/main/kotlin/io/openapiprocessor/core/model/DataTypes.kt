/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.model

import io.openapiprocessor.core.model.datatypes.*

/**
 * Container of data types from OpenAPI '#/component/schemas'.
 */
class DataTypes {

    class DataTypeInfo(val dataType: DataType)

    private val dataTypeInfos: MutableMap<String, DataTypeInfo> = mutableMapOf()

    /**
     * provides all named data types (including simple data types) used by the api endpoint.
     *
     * @return list of data types
     */
    fun getDataTypes(): Collection<DataType> {
        return dataTypeInfos.values
            .filter { it.dataType !is MappedDataType }
            .map { it.dataType }
    }

    /**
     * provides the *object* data types (model classes) used by the api endpoints.
     * For this objects the processor will create POJOs classes.
     *
     * @return list of object data types
     */
    @Deprecated("use getModelDataTypes()")
    fun getObjectDataTypes(): Collection<ObjectDataType> {
        return dataTypeInfos.values
            .filter { it.dataType is ObjectDataType }
            .map { it.dataType as ObjectDataType }
    }

    /**
     * provides the *object* data types (model classes) used by the api endpoints.
     * For this objects the processor will create POJOs classes.
     *
     * experimental: will probably replace getObjectDataTypes().
     *
     * @return list of object data types
     */
    fun getModelDataTypes(): Collection<ModelDataType> {
        return dataTypeInfos.values
            .filter { it.dataType is ModelDataType && it.dataType.isModel() }
            .map { it.dataType as ModelDataType }
    }

    /**
     * provides the enum data types (model classes) used by the api endpoints.
     * For this objects the processor will create enum classes.
     *
     * @return list of enum data types
     */
    fun getEnumDataTypes(): Collection<StringEnumDataType> {
        return dataTypeInfos.values
            .filter { it.dataType is StringEnumDataType }
            .map { it.dataType as StringEnumDataType }
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
        dataTypeInfos[name] = DataTypeInfo(dataType)
    }

    /**
     * find data type by name.
     *
     * @param name the name
     * @return the data type or null if not found
     */
    fun find (name: String): DataType {
        return dataTypeInfos[name]?.dataType!!
    }

}
