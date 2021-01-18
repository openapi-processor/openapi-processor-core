/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.support

import io.openapiprocessor.core.model.HttpMethod
import io.openapiprocessor.core.parser.OpenApi
import io.openapiprocessor.core.parser.Schema

/**
 * extracts a specific response Schema from an [OpenApi] object created by [parse()][parse].
 *
 * @param path the endpoint path
 * @param method the http method
 * @param status the http response status
 */
fun OpenApi.getResponseSchema(path: String, method: HttpMethod, status: String, mediaType: String)
: Schema {
    val endpoint = this.getPaths()[path]
    if (endpoint == null) {
        println("unknown path '$path' !")
    }

    val operation = endpoint?.getOperations()?.find { it.getMethod() == method }
    if (operation == null) {
        println("unknown method '$method' ($path)!")
    }

    val response = operation?.getResponses()?.get(status)
    if (response == null) {
        println("unknown response status '$status' ($path $method)!")
    }

    val media = response?.getContent()?.get(mediaType)
    if (media == null) {
        println("unknown media type '$mediaType' ($path $method $status)!")
    }

    return media?.getSchema()!!
}

