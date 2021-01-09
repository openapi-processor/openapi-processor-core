/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.model

import io.openapiprocessor.core.model.datatypes.DataType
import io.openapiprocessor.core.model.datatypes.NoneDataType

/**
 * simpler setup of an empty response.
 */
class EmptyResponse(
    contentType: String = "?",
    responseType: DataType = NoneDataType(),
    description: String? = null
): Response(contentType, responseType, description)
