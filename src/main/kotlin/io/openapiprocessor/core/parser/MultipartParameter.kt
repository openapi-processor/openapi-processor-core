/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser

/**
 * "fake" parameter for framework specific annotation selection
 */
class MultipartParameter(val parameter: String, val contentType: String): Parameter {

    override fun getIn(): String {
        return "multipart"
    }

    override fun getName(): String {
        return parameter
    }

    override fun getSchema(): Schema {
        return null!!
    }

    override fun isRequired(): Boolean {
        return true
    }

    override fun isDeprecated(): Boolean {
        return false
    }

    override val description: String?
        get() = null

}
