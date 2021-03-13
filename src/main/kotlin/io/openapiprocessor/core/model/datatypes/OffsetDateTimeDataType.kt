/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.model.datatypes

import io.openapiprocessor.core.model.Documentation

/**
 * OpenAPI type 'string' with format 'date-time' maps to java OffsetDateTime.
 */
class OffsetDateTimeDataType(

    constraints: DataTypeConstraints? = null,
    deprecated: Boolean = false,
    documentation: Documentation? = null

): DataTypeBase(constraints, deprecated) {

    override fun getName(): String {
        return "OffsetDateTime"
    }

    override fun getPackageName(): String {
        return "java.time"
    }

}
