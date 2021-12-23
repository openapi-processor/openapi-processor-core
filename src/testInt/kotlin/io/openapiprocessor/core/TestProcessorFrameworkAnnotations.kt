/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core

import io.openapiprocessor.core.framework.FrameworkAnnotation
import io.openapiprocessor.core.framework.FrameworkAnnotations
import io.openapiprocessor.core.model.HttpMethod
import io.openapiprocessor.core.model.parameters.Parameter

val MAPPING = FrameworkAnnotation("Mapping", "annotation")
val PARAMETER = FrameworkAnnotation("Parameter", "annotation")

/**
 * simple [io.openapiprocessor.core.framework.FrameworkAnnotations] implementation for testing.
 */
class TestFrameworkAnnotations: FrameworkAnnotations {

    override fun getAnnotation(httpMethod: HttpMethod): FrameworkAnnotation {
        return MAPPING
    }

    override fun getAnnotation(parameter: Parameter): FrameworkAnnotation {
        return PARAMETER
    }
}
