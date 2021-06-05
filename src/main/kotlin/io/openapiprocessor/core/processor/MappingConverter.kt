/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.processor

import io.openapiprocessor.core.converter.mapping.Mapping
import io.openapiprocessor.core.processor.mapping.v1.MappingConverter as MappingConverterV1
import io.openapiprocessor.core.processor.mapping.v1.Mapping as MappingV1
import io.openapiprocessor.core.processor.mapping.MappingVersion
import io.openapiprocessor.core.processor.mapping.v2.MappingConverter as MappingConverterV2
import io.openapiprocessor.core.processor.mapping.v2.Mapping as MappingV2

/**
 *  Converter for the type mapping from the mapping yaml. It converts the type mapping information
 *  into the format used by [io.openapiprocessor.core.converter.DataTypeConverter].
 */
class MappingConverter {

   fun convert(source: MappingVersion?): List<Mapping> {
       if (source == null) {
           return emptyList()
       }

       return if (source.v2) {
           MappingConverterV2(source as MappingV2).convert()
       } else {
           MappingConverterV1().convert(source as MappingV1)
       }
   }

}
