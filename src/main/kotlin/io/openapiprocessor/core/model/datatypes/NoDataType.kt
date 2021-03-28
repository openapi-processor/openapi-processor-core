/*
 * Copyright © 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.model.datatypes

class NoDataType(

    private val type: String,
    override val constraints: DataTypeConstraints? = null,
    override val deprecated: Boolean = false

): DataTypeBase() {

    override fun getName(): String {
        return type
    }

    override fun getPackageName(): String {
        return "io.openapiprocessor.leaked"
    }

}


