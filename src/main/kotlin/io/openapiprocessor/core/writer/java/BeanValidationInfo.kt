/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

class BeanValidationInfo(
    val typeName: String,
    val imports: Set<String>,
    val annotations: List<String>
) {
    val hasAnnotations
        get() = annotations.isNotEmpty()
}
