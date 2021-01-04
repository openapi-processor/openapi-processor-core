/*
 * Copyright © 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser

import io.openapiprocessor.core.model.HttpMethod

/**
 * OpenAPI Operation abstraction.
 */
interface Operation {

    fun getMethod(): HttpMethod
    fun getOperationId(): String?
    fun getParameters(): List<Parameter>

    fun getRequestBody(): RequestBody?
    fun getResponses(): Map<String, Response>

    fun isDeprecated(): Boolean
    fun hasTags(): Boolean
    val description: String?

    fun getFirstTag(): String?

}
