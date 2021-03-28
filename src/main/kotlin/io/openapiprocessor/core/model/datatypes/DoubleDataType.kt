/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.model.datatypes

import io.openapiprocessor.core.model.Documentation

/**
 * OpenAPI type 'number' with format 'double' maps to java Double.
 */
class DoubleDataType(

    override val constraints: DataTypeConstraints? = null,
    override val deprecated: Boolean = false,
    override val documentation: Documentation? = null

): DataTypeBase() {

    override fun getName(): String {
        return "Double"
    }

    override fun getPackageName(): String {
        return "java.lang"
    }

}
