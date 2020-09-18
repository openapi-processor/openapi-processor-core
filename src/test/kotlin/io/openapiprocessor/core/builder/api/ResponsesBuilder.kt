/*
 * Copyright © 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.builder.api

import io.openapiprocessor.core.model.Response

class ResponsesBuilder {
    private val responses: MutableMap<String, List<Response>> = linkedMapOf()

    fun status(status: String = "200", init: ResponseBuilder.() -> Unit? = {}) {
        val builder = ResponseBuilder()
        init(builder)
        responses[status] = builder.build()
    }

    fun build(): Map<String, List<Response>> {
        return responses
    }

}
