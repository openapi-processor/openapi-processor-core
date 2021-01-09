/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.builder.api

import io.openapiprocessor.core.model.Response
import io.openapiprocessor.core.model.datatypes.DataType
import io.openapiprocessor.core.model.datatypes.NoneDataType
import io.openapiprocessor.core.model.EmptyResponse

class ResponseDetailBuilder {
    var description: String? = null

    fun description(description: String) {
        this.description = description
    }

}

class ResponseBuilder {
    private val responses: MutableList<Response> = mutableListOf()

    fun response(contentType: String? = null, dataType: DataType? = null, init: ResponseDetailBuilder.() -> Unit? = {}) {
        val builder = ResponseDetailBuilder()
        init(builder)

        lateinit var response: Response

        if(contentType == null && dataType == null)
            response = EmptyResponse(
                description = builder.description)
        else
            response = Response(
                contentType ?: "none",
                dataType ?: NoneDataType(),
                builder.description)

        responses.add(response)
    }

    fun build(): List<Response> {
        return  responses
    }

}
