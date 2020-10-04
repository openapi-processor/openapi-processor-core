/*
 * Copyright © 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.processor.mapping.v2.parser

/**
 * extracted data from parser
 */
data class ToData(
    /**
     * target type
     */
    val type: String,

    /**
     * target type generic arguments
     */
    var typeArguments: List<String> = emptyList(),

    /**
     * annotation on target type
     */
    var annotationType: String? = null,

    /**
     * all parameters of the annotation, pass through
     */
    var annotationParameters: String? = null

)
