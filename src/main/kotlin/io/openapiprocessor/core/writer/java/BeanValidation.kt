/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

enum class BeanValidation(val typeName: String) {
    DECIMAL_MAX("javax.validation.constraints.DecimalMax"),
    DECIMAL_MIN("javax.validation.constraints.DecimalMin"),
    NOT_NULL("javax.validation.constraints.NotNull"),
    PATTERN("javax.validation.constraints.Pattern"),
    SIZE("javax.validation.constraints.Size"),
    VALID("javax.validation.Valid");

    val import = typeName
    val annotation = "@" + typeName.substring(typeName.lastIndexOf('.') + 1)
}
