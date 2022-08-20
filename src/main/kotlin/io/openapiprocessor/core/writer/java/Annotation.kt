/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

class Annotation(
    val qualifiedName: String,
    val parameters: LinkedHashMap<String, String> = linkedMapOf()
) {
    val typeName: String
        get() {
            return qualifiedName.substring(qualifiedName.lastIndexOf('.') + 1)
        }

    val packageName: String
        get() {
            return qualifiedName.substring(0, qualifiedName.lastIndexOf('.') + 1)
        }

    val import = qualifiedName

    @Deprecated("use AnnotationWriter")
    val annotation: String
        get() {
            var result = "@" + qualifiedName.substring(qualifiedName.lastIndexOf('.') + 1)
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
