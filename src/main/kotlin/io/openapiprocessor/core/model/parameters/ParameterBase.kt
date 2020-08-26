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

package io.openapiprocessor.core.model.parameters

import io.openapiprocessor.core.model.datatypes.DataType

/**
 * basic Parameter implementation. Reduces duplication.
 */
abstract class ParameterBase(

    override val name: String,
    override val dataType: DataType,
    override val required: Boolean = false,
    override val deprecated: Boolean = false

): Parameter {

    override val dataTypeImports: Set<String>
        get() = dataType.getImports()

    override val constraints: ParameterConstraints
        get() = ParameterConstraints(dataType.getConstraints())

    override val withAnnotation: Boolean
        get() = true

    override val withParameters: Boolean
        get() = true

}
