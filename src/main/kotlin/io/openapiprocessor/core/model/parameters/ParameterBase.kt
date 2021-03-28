/*
 * Copyright © 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
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
    override val deprecated: Boolean = false,
    override var description: String? = null

): Parameter {

    override val dataTypeImports: Set<String>
        get() = dataType.getImports()

    override val constraints: ParameterConstraints
        get() = ParameterConstraints(dataType.constraints)

    override val withAnnotation: Boolean
        get() = true

    override val withParameters: Boolean
        get() = true

}
