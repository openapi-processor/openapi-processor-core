/*
 * Copyright 2020 the original authors
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

package io.openapiprocessor.core.converter.wrapper

import io.openapiprocessor.core.converter.ApiOptions
import io.openapiprocessor.core.converter.SchemaInfo
import io.openapiprocessor.core.converter.mapping.*
import io.openapiprocessor.core.model.datatypes.DataType
import io.openapiprocessor.core.model.datatypes.NoneDataType
import io.openapiprocessor.core.model.datatypes.ResultDataType

/**
 * wraps the result data type with the mapped result type.
 *
 * @author Martin Hauner
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
        val endpointMatches = finder.findEndpointResultMapping(info)

        if (endpointMatches.isNotEmpty()) {

            if (endpointMatches.size != 1) {
                throw AmbiguousTypeMappingException (endpointMatches.map { it as TypeMapping })
            }

            val target = (endpointMatches.first() as TargetTypeMapping).getTargetType()
            if (target != null) {
                return target
            }
        }

        // find global result mapping
        val typeMatches = finder.findResultMapping(info)
        if (typeMatches.isEmpty ()) {
            return null
        }

        if (typeMatches.size != 1) {
            throw AmbiguousTypeMappingException(typeMatches.map { it as TypeMapping })
        }

        val match = typeMatches.first () as TargetTypeMapping
        return match.getTargetType()
    }

}
