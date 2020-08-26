/*
 * Copyright © 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.builder.api

import io.openapiprocessor.core.model.HttpMethod

/**
 * entry point of model [Endpoint] builder dsl
 */
fun endpoint(path: String, init: EndpointBuilder.() -> Unit): Endpoint {
    val builder = EndpointBuilder(path)
    init(builder)
    return builder.build()
}

class EndpointBuilder(
    private val path: String,
) {
    private var method = HttpMethod.GET
    private var deprecated = false

    fun get() {
        method = HttpMethod.GET
    }

    fun deprecated() {
        deprecated = true
    }

    fun build(): Endpoint {
        return Endpoint(path, deprecated)
    }
}


// dummy ... can't use Endpoint, it is still groovy code
class Endpoint(
    val path: String,
    val deprecated: Boolean
)
