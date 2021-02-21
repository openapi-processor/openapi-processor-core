/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping.matcher

import io.openapiprocessor.core.converter.mapping.TypeMapping

/**
 * [io.openapiprocessor.core.converter.mapping.MappingFinder] matcher for null type mapping.
 */
class NullTypeMatcher: (TypeMapping) -> Boolean {

    override fun invoke(mapping: TypeMapping): Boolean {
        return mapping.sourceTypeName == "null"
    }

}
