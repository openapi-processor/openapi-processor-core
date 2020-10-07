/*
 * Copyright © 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser.openapi4j

import org.openapi4j.parser.model.v3.OpenApi3
import org.openapi4j.parser.model.v3.Path as O4jPath

/**
 * openapi4j $ref resolver on o4j types.
 */
class RefResolverNative(private val api: OpenApi3) {

    fun resolve(path: O4jPath): O4jPath {
        return path.getReference(api.context).getMappedContent(O4jPath::class.java)
    }

}
