/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.wrapper

import io.openapiprocessor.core.converter.ApiOptions
import io.openapiprocessor.core.converter.SchemaInfo
import io.openapiprocessor.core.converter.mapping.MappingFinder
import io.openapiprocessor.core.converter.mapping.TargetType
import io.openapiprocessor.core.model.datatypes.DataType
import io.openapiprocessor.core.model.datatypes.NoneDataType
import io.openapiprocessor.core.model.datatypes.NullDataType

/**
 * wraps a data type in [io.openapiprocessor.core.model.datatypes.NullDataType].
 */
class NullDataTypeWrapper(
    private val options: ApiOptions,
    private val finder: MappingFinder = MappingFinder(options.typeMappings)
) {

    /**
     * wraps any data type with the configured null data type like
     * `org.openapitools.jackson.nullable.JsonNullable`.
     *
     * @param dataType the data type to wrap
     * @param schemaInfo the open api type with context information
     * @return the resulting java data type
     */
    fun wrap(dataType: DataType, schemaInfo: SchemaInfo): DataType {
        val targetType = getNullDataType(schemaInfo)
        if (targetType == null) {
            return dataType
        }

        val wrappedType = NullDataType (
            targetType.getName(),
            targetType.getPkg(),
            checkNone (dataType)
        )

        return wrappedType
    }

    private fun getNullDataType(info: SchemaInfo): TargetType? {
        // check endpoint result mapping
        return finder.findEndpointNullTypeMapping(info)?.getTargetType()

        // not yet supported
        // check global result mapping
        // return finder.findNullTypeMapping()?.getTargetType()
    }

    private fun checkNone(dataType: DataType): DataType {
        if (dataType is NoneDataType) {
            return dataType.wrappedInResult ()
        }

        return dataType
    }

}
