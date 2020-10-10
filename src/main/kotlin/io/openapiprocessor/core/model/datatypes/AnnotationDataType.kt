/*
 * Copyright © 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.model.datatypes

/**
 * additional annotation type.
 */
class AnnotationDataType(
    private val type: String,
    private val pkg: String,
    private val parameters: String?
): DataTypeBase() {

    override fun getName(): String {
        return type
    }

    override fun getPackageName(): String {
        return pkg
    }

    fun getParameters(): String {
        return parameters ?: ""
    }

}
