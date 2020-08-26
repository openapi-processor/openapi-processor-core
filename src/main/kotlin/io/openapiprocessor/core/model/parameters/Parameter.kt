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
 * Parameter model of an OpenAPI parameter.
 *
 * @author Martin Hauner
 */
interface Parameter {

    /**
     * the name of the parameter.
     */
    val name: String

    /**
     * the data type of the parameter.
     */
    val dataType: DataType

    /**
     * the imports required for the data type of the parameter.
     */
    val dataTypeImports: Set<String>

    /**
     * the constraints of the parameter, if any.
     */
    val constraints: ParameterConstraints

    /**
     * true if the parameter is required, else false.
     */
    val required: Boolean

    /**
     * true if the parameter is deprecated, else false.
     */
    val deprecated: Boolean

    /**
     * true if the parameter requires an annotation, else false. Some parameters don't need an
     * annotation.
     */
    val withAnnotation: Boolean

    /**
     * true if the annotation requires parameters, else false. Some parameters require an annotation
     * but without any parameters.
     */
    val withParameters: Boolean

}
