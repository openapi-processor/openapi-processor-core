/*
 * Copyright © 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.model.parameters

import io.openapiprocessor.core.model.datatypes.DataType

/**
 * OpenAPI cookie parameter mode.
 */
class CookieParameter(

    name: String,
    dataType: DataType,
    required: Boolean = false,
    deprecated: Boolean = false,
    description: String? = null

): ParameterBase(name, dataType, required, deprecated, description)
