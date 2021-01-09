/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.builder.api

class ParameterBuilder {
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

