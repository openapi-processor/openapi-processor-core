/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.model.datatypes

import io.openapiprocessor.core.converter.SchemaInfo
import io.openapiprocessor.core.model.DataTypes
/**
 * OpenAPI $ref type that is lazily evaluated. It is used to break loops in the schema definitions.
 */
class LazyDataType(
    private val info: SchemaInfo,
    private val dataTypes: DataTypes
): DataTypeBase() {

    override fun getName(): String {
        return dataType.getName()
    }

    override fun getPackageName(): String {
        return  dataType.getPackageName()
    }

    override fun getImports(): Set<String> {
        return dataType.getImports()
    }

    override val referencedImports: Set<String>
        get() = dataType.referencedImports

    private val dataType: DataType
        get() = dataTypes.find(info.getName())!!

}
