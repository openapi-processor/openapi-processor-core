/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.model

import io.openapiprocessor.core.model.datatypes.DataType

/**
 * Endpoint response properties.
 */
open class Response(
    val contentType: String,
    val responseType: DataType,
    val description: String? = null
) {

    val imports
        get() = responseType.getImports()

    val empty
        get() = contentType == "?"

}
