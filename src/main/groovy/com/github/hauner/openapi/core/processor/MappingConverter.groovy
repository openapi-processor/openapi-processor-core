/*
 * Copyright 2019-2020 the original authors
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

package com.github.hauner.openapi.core.processor

import io.openapiprocessor.core.converter.mapping.Mapping
import com.github.hauner.openapi.core.processor.mapping.MappingConverter as MappingConverterV1
import com.github.hauner.openapi.core.processor.mapping.Mapping as MappingV1
import com.github.hauner.openapi.core.processor.mapping.VersionedMapping
import io.openapiprocessor.core.converter.mapping.v2.MappingConverter as MappingConverterV2
import io.openapiprocessor.core.converter.mapping.v2.Mapping as MappingV2

/**
 *  Converter for the type mapping from the mapping yaml. It converts the type mapping information
 *  into the format used by {@link com.github.hauner.openapi.core.converter.DataTypeConverter}.
 *
 *  @author Martin Hauner
 */
class MappingConverter {

    List<Mapping> convert (VersionedMapping source) {
        if (source?.isV2()) {
            def converter = new MappingConverterV2 ()
            converter.convert (source as MappingV2)
        } else {
            def converter = new MappingConverterV1 ()
            converter.convert (source as MappingV1)
        }
    }

}
