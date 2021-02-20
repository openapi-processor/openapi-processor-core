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
import io.openapiprocessor.core.model.datatypes.ResultDataType

/**
 * wraps the result data type with the mapped result type.
 */
class ResultDataTypeWrapper(
    private val options: ApiOptions,
    private val finder: MappingFinder = MappingFinder(options.typeMappings)
) {

    /**
     * wraps a (converted) result data type with the configured result java data type like
     * {@code ResponseEntity}.
     *
     * If the configuration for the result type is 'plain' the source data type is not wrapped.
     *
     * @param dataType the data type to wrap
     * @param schemaInfo the open api type with context information
     * @return the resulting java data type
     */
    fun wrap(dataType: DataType, schemaInfo: SchemaInfo): DataType{
        val targetType = getMappedResultDataType(schemaInfo)
        if (targetType == null) {
            return dataType
        }

        if (targetType.typeName == "plain") {
            return dataType

        } else {
            val resultType = ResultDataType (
                targetType.getName(),
                targetType.getPkg(),
                checkNone (dataType)
            )
            return resultType
        }
    }

    private fun checkNone(dataType: DataType): DataType {
        if (dataType is NoneDataType) {
            return dataType.wrappedInResult ()
        }

        return dataType
    }

    private fun getMappedResultDataType(info: SchemaInfo): TargetType? {
        // check endpoint result mapping
        val epMatch = finder.findEndpointResultTypeMapping(info)
        if (epMatch != null) {
            return epMatch.getTargetType()
        }

        // check global result mapping
        return finder.findResultTypeMapping()?.getTargetType()
    }

}
