/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser.openapi

import java.net.URL

interface Validation {

    fun validate(context: ValidationContext, node: Map<String, Any?>): List<ValidationMessage>

}

class ValidationContext(val source: URL, val path: String = """$""") {

    fun propertyPath(property: String): String {
        return "$path.$property"
    }

}

class ValidationMessage(
    // warn/error ?
    // file ?
    val path: String,
    val text: String
)

class Validator(val source: URL, private val validations: List<Validation>) {

    fun validate(node: Map<String, Any>): List<ValidationMessage> {
        return emptyList()
    }

}
