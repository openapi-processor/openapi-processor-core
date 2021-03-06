/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.builder.api

import io.openapiprocessor.core.model.datatypes.AnnotationDataType

open class ParameterBuilder {
    var required: Boolean = false
    var deprecated: Boolean = false
    var description: String? = null

    fun required() {
        required = true
    }

    fun deprecated() {
        deprecated = true
    }

    fun description(description: String) {
        this.description = description
    }

}

class AddParameterBuilder: ParameterBuilder() {
    var annotation: AnnotationDataType? = null
}
