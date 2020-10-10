/*
 * Copyright © 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping

/**
 * (additional) annotation from mapping
 */
class Annotation(

    /**
     * additional annotation of parameter.
     */
    val type: String,

    /**
     * all parameters of the annotation (pass through).
     */
    val parameters: String? = null

)
