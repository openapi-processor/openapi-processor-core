/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.wrapper

import io.openapiprocessor.core.converter.ApiOptions
import io.openapiprocessor.core.converter.SchemaInfo
import io.openapiprocessor.core.converter.mapping.*
import io.openapiprocessor.core.model.datatypes.DataType
import io.openapiprocessor.core.model.datatypes.NoneDataType
import io.openapiprocessor.core.model.datatypes.SingleDataType

/**
 * wraps the data type with the 'singe' data mapping.
 *
 * Used to wrap Responses or RequestBody's with {@code Mono<>} or similar types.
 */
class SingleDataTypeWrapper(
    private val options: ApiOptions,
    private val finder: MappingFinder = MappingFinder(options.typeMappings)
) {

    /**
     * wraps a (converted) non-array data type with the configured single data type like
     * {@code Mono<>} etc.
     *
     * If the configuration for the single type is 'plain' or not defined the source data type
     * is not wrapped.
     *
     * @param dataType the data type to wrap
     * @param schemaInfo the open api type with context information
     * @return the resulting java data type
     */
    fun wrap(dataType: DataType, schemaInfo: SchemaInfo): DataType {
        val targetType = getSingleResultDataType (schemaInfo)
        if (targetType == null || schemaInfo.isArray ()) {
            return dataType
        }

        if (targetType.typeName == "plain") {
            return dataType
        }

        val wrappedType = SingleDataType (
            targetType.getName(),
            targetType.getPkg(),
            checkNone (dataType)
        )

        return wrappedType
    }

    private fun getSingleResultDataType(info: SchemaInfo): TargetType? {
        // check endpoint result mapping
        val epMatch = finder.findEndpointSingleTypeMapping(info)
        if (epMatch != null) {
            return epMatch.getTargetType()
        }

        // check global result mapping
        return finder.findSingleTypeMapping()?.getTargetType()
    }

    private fun checkNone(dataType: DataType): DataType {
        if (dataType is NoneDataType) {
            return dataType.wrappedInResult ()
        }

        return dataType
    }

}
