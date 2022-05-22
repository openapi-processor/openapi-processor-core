/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

@file:Suppress("DEPRECATION")

package io.openapiprocessor.core.parser.openapi.validations

import io.openapiprocessor.core.parser.openapi.Validation
import io.openapiprocessor.core.parser.openapi.ValidationContext
import io.openapiprocessor.core.parser.openapi.ValidationMessage

@Deprecated("obsolete")
class VersionValidator: Validation {

    override fun validate(context: ValidationContext, node: Map<String, Any?>): List<ValidationMessage> {
        val messages = mutableListOf<ValidationMessage>()

        val version = node[OPENAPI]
        if (version == null || version !is String || !(VERSION matches version)) {
            messages.add(ValidationMessage(context.propertyPath(OPENAPI), message(version)))
        }

        return messages
    }

    fun message(value: Any?): String {
        return "'$value' is no valid version number"
    }

    companion object {
        const val OPENAPI = "openapi"
        val VERSION = Regex("""\d\.\d\.\d""")
    }

}
