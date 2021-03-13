/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.model.datatypes

import io.openapiprocessor.core.model.Documentation

/**
 * OpenAPI type 'boolean' maps to java Boolean.
 */
class BooleanDataType(

   constraints: DataTypeConstraints? = null,
   deprecated: Boolean = false,
   documentation: Documentation? = null

): DataTypeBase(constraints, deprecated, documentation) {

    override fun getName(): String {
        return "Boolean"
    }

    override fun getPackageName(): String {
        return "java.lang"
    }

}
