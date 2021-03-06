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

package io.openapiprocessor.core.model.datatypes

/**
 * OpenAPI schema mapped to a map java type.
 *
 * only used as marker for {@link com.github.hauner.openapi.core.model.parameters.Parameter}
 * {@code withAnnotation()}
 *
 * @author Martin Hauner
 */
@Deprecated(message = "not needed anymore")
class MappedMapDataType(
    type: String,
    pkg: String,
    genericTypes: List<String> = emptyList(),
    constraints: DataTypeConstraints? = null,
    deprecated: Boolean = false
) : MappedDataType(type, pkg, genericTypes, constraints, deprecated)
