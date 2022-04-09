/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.model

import io.openapiprocessor.core.model.datatypes.InterfaceDataType
import io.openapiprocessor.core.model.datatypes.ModelDataType
import io.openapiprocessor.core.model.datatypes.StringEnumDataType
import java.util.function.Consumer

/**
 * Root of the internal model used to generate the api.
 */
class Api(
    private /*val*/ var interfaces: List<Interface> = emptyList(),

    /**
     * named data types (i.e. $ref) used in the OpenAPI description.
     */
    private val dataTypes: DataTypes = DataTypes()
) {

    fun getInterface(name: String): Interface {
        return interfaces.find { it.name.equals(name, ignoreCase = true) }!!
    }

    fun setInterfaces(ifs: List<Interface>) {
        interfaces = ifs
    }

    fun getDataTypes(): DataTypes {
        return dataTypes
    }

    fun forEachInterface(action: Consumer<Interface>) {
        interfaces.forEach(action)
    }

    fun forEachModelDataType(action: Consumer<ModelDataType>) {
        dataTypes.getModelDataTypes().forEach(action)
    }

    fun forEachInterfaceDataType(action: Consumer<InterfaceDataType>) {
        dataTypes.getInterfaceDataTypes().forEach(action)
    }

    fun forEachEnumDataType(action: Consumer<StringEnumDataType>) {
        dataTypes.getEnumDataTypes().forEach(action)
    }

}
