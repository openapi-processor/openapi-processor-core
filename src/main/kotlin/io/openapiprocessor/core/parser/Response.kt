/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser

/**
 * OpenAPI Response abstraction.
 */
interface Response {

    fun getContent(): Map<String, MediaType>
    val description: String?

}
