/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

class Annotation(
    private val typeName: String,
    private val parameters: LinkedHashMap<String, String> = linkedMapOf()
) {
    val import = typeName

    val annotation: String
        get() {
            var result = "@" + typeName.substring(typeName.lastIndexOf('.') + 1)
            if (parameters.isNotEmpty()) {
                result += "("
                result += parameters
                    .map { e -> "${e.key} = ${e.value}"}
                    .joinToString(", ")
                result += ")"
            }
            return result
        }
}
