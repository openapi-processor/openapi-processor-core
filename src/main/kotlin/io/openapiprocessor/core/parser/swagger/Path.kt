/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser.swagger

import io.openapiprocessor.core.parser.Path as ParserPath
import io.openapiprocessor.core.model.HttpMethod
import io.openapiprocessor.core.parser.Operation as ParserOperation
import io.swagger.v3.oas.models.PathItem as SwaggerPath
import io.swagger.v3.oas.models.Operation as SwaggerOperation

/**
 * Swagger Path abstraction.
 */
class Path(private val path: String, private val info: SwaggerPath): ParserPath {

    override fun getPath(): String = path

    override fun getOperations(): List<ParserOperation> {
        val ops: MutableList<ParserOperation> = mutableListOf()

        HttpMethod.values().map {
            val op = info.getOperation(it.method)
            if (op != null) {
                ops.add (Operation(it, op, info))
            }
        }

        return ops
    }

}

fun SwaggerPath.getOperation(method: String): SwaggerOperation? {
    return when(method) {
        HttpMethod.GET.method -> this.get
        HttpMethod.PUT.method -> this.put
        HttpMethod.POST.method -> this.post
        HttpMethod.DELETE.method -> this.delete
        HttpMethod.OPTIONS.method -> this.options
        HttpMethod.HEAD.method -> this.head
        HttpMethod.PATCH.method -> this.patch
        HttpMethod.TRACE.method -> this.trace
        else -> null
    }
}
