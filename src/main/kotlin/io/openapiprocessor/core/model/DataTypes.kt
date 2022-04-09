/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.model

import io.openapiprocessor.core.model.datatypes.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Container of data types from OpenAPI '#/component/schemas'.
 */
class DataTypes {
    private val log: Logger = LoggerFactory.getLogger(this.javaClass.name)

    class DataTypeInfo(val dataType: DataType, var refCount: Long = 0) {

        fun addRef() {
            refCount++
        }

    }

    private val dataTypeInfos: MutableMap<String, DataTypeInfo> = mutableMapOf()

    /** test only
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
     * For these objects the processor will create POJOs classes.
     *
     * @return list of object data types
     */
    fun getModelDataTypes(): Collection<ModelDataType> {
        return dataTypeInfos.values
            .filter { it.dataType is ModelDataType }
            .filter { it.refCount > 0 }
            .map { it.dataType as ModelDataType }
    }

    /**
     * provides the enum data types (model classes) used by the api endpoints.
     * For these objects the processor will create enum classes.
     *
     * @return list of enum data types
     */
    fun getEnumDataTypes(): Collection<StringEnumDataType> {
        return dataTypeInfos.values
            .filter { it.dataType is StringEnumDataType }
            .filter { it.refCount > 0 }
            .map { it.dataType as StringEnumDataType }
    }

    /**
     * provides the *interface* data types (model interfaces) used by the api endpoints.
     * For these objects the processor will create POJOs interfaces.
     *
     * @return list of object data types
     */
    fun getInterfaceDataTypes(): Collection<InterfaceDataType> {
        return dataTypeInfos.values
            .filter { it.dataType is InterfaceDataType }
            .filter { it.refCount > 0 }
            .map { it.dataType as InterfaceDataType }
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
     * remove a data type.
     *
     * @param dataType the source data type
     */
    fun del(dataType: DataType) {
        dataTypeInfos.remove (dataType.getName())
    }

    /**
     * find data type by name.
     *
     * @param name the data type name
     * @return the data type
     */
    fun find(name: String): DataType? {
        return dataTypeInfos[name]?.dataType
    }

    /**
     * increment usage count of data type.
     *
     * @param name the data type name
     */
    fun addRef(name: String) {
        val info: DataTypeInfo? = dataTypeInfos[name]
        if (info == null) {
            log.error("unknown data type $name")
            return
        }

        info.addRef()
        log.info("ref $name ${info.refCount}")
    }

    val size: Int
        get() = dataTypeInfos.size

    /**
     * test.
     */
    fun getRefCnt(name: String): Long {
        return dataTypeInfos[name]?.refCount!!
    }

    /**
     * debug.
     */
    fun print() {
        dataTypeInfos.forEach {
            println("${it.key} (${it.value.dataType.getPackageName()}) ${it.value.refCount}")
        }
    }

}
