/*
 * Copyright © 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.model.parameters

import io.openapiprocessor.core.model.datatypes.DataType

/**
 * Parameter model of an OpenAPI parameter.
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
     * (optional) parameter description
     */
    val description: String?

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
