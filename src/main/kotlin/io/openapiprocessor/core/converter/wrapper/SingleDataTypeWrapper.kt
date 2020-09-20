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
import io.openapiprocessor.core.model.datatypes.SingleDataType

/**
 * wraps the data type with the 'singe' data mapping.
 *
 * Used to wrap Responses or RequestBody's with {@code Mono<>} or similar types.
 *
 * @author Martin Hauner
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
        // check endpoint single mapping
        val endpointMatches= finder.findEndpointSingleMapping(info)

        if (endpointMatches.isNotEmpty()) {

            if (endpointMatches.size != 1) {
                throw AmbiguousTypeMappingException(endpointMatches.map { it as TypeMapping })
            }

            val target = (endpointMatches.first() as TargetTypeMapping).getTargetType()
            if (target != null) {
                return target
            }
        }

        // find global single mapping
        val typeMatches = finder.findSingleMapping(info)
        if (typeMatches.isEmpty ()) {
            return null
        }

        if (typeMatches.size != 1) {
            throw AmbiguousTypeMappingException (typeMatches.map { it as TypeMapping })
        }

        val match = typeMatches.first () as TargetTypeMapping
        return match.getTargetType()
    }

    private fun checkNone(dataType: DataType): DataType {
        if (dataType is NoneDataType) {
            return dataType.wrappedInResult ()
        }

        return dataType
    }

}
