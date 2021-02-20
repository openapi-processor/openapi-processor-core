/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping.matcher

import io.openapiprocessor.core.converter.mapping.EndpointTypeMapping

/**
 * [io.openapiprocessor.core.converter.mapping.MappingFinder] matcher for endpoint type mappings.
 */
class EndpointTypeMatcher(private val path: String): (EndpointTypeMapping) -> Boolean {

    override fun invoke(m: EndpointTypeMapping): Boolean {
        return m.path == path
    }

}
