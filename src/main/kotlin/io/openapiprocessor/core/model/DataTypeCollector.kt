/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.model

import io.openapiprocessor.core.model.datatypes.*

/**
 * ref counts used model data types honoring data type mappings to generate only required (used)
 * classes.
 */
class DataTypeCollector(
    private val dataTypes: DataTypes, private val generatedPackageName: String) {

    fun collect(dataType: DataType) {
        when (dataType) {
            is ArrayDataType -> {
                collect(dataType.item)
            }
            is MappedCollectionDataType -> {
                collect(dataType.item)
            }
            is ObjectDataType -> {
                dataTypes.addRef(dataType.getName())
                dataType.forEach { _, propDataType ->
                    collect(propDataType)
                }
            }
            is MappedDataType -> {
                dataType.genericTypes
                    .filter { it.startsWith(generatedPackageName) }
                    .forEach {
                        val name = it.substringAfterLast(".")
                        val found = dataTypes.find(name)
                        if (found != null) {
                            collect(found)
                        }
                    }
            }
            is AllOfObjectDataType -> {
                dataTypes.addRef(dataType.getName())
                dataType.forEach { _, propDataType ->
                    collect(propDataType)
                }
            }
            is AnyOneOfObjectDataType -> {
                dataType.forEach { ofDataType ->
                    collect(ofDataType)
                }
            }
            is StringEnumDataType -> {
                dataTypes.addRef(dataType.getName())
            }
            is PropertyDataType -> {
                collect(dataType.dataType)
            }
            is InterfaceDataType -> {
                dataTypes.addRef(dataType.getName())
                dataType.items.forEach {
                    collect(it)
                }
            }
        }
    }

}
