/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser.openapi.v31

import io.openapiparser.model.v31.PathItem
import io.openapiparser.model.v31.Operation
import io.openapiprocessor.core.model.HttpMethod
import io.openapiprocessor.core.parser.Operation as ParserOperation
import io.openapiprocessor.core.parser.Path as ParserPath

/**
 * openapi.parser Path abstraction.
 */
class Path(
    private val path: String,
    private val info: PathItem
) : ParserPath {

    override fun getPath(): String = path

    override fun getOperations(): List<ParserOperation> {
        var pathItem = info
        if (info.isRef) {
            pathItem = info.refObject
        }

        val result: MutableList<ParserOperation> = mutableListOf()
        collectNotNull(HttpMethod.GET, pathItem.get, pathItem, result)
        collectNotNull(HttpMethod.PUT, pathItem.put, pathItem, result)
        collectNotNull(HttpMethod.POST, pathItem.post, pathItem, result)
        collectNotNull(HttpMethod.DELETE, pathItem.delete, pathItem, result)
        collectNotNull(HttpMethod.OPTIONS, pathItem.options, pathItem, result)
        collectNotNull(HttpMethod.PATCH, pathItem.patch, pathItem, result)
        collectNotNull(HttpMethod.TRACE, pathItem.trace, pathItem, result)
        collectNotNull(HttpMethod.HEAD, pathItem.head, pathItem, result)
        return result
    }

    private fun collectNotNull(method: HttpMethod, operation: Operation?, pathItem: PathItem, target: MutableList<ParserOperation>) {
        if (operation == null)
            return

        target.add(Operation(method, operation, pathItem))
    }
}
