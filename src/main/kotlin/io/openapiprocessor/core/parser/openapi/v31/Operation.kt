/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser.openapi.v31

import io.openapiprocessor.core.model.HttpMethod
import io.openapiparser.model.v31.Operation as Operation31
import io.openapiparser.model.v31.Parameter as Parameter31
import io.openapiparser.model.v31.PathItem as Path31
import io.openapiparser.model.v31.Response as Response31
import io.openapiprocessor.core.parser.Operation as ParserOperation
import io.openapiprocessor.core.parser.Parameter as ParserParameter
import io.openapiprocessor.core.parser.RequestBody as ParserRequestBody
import io.openapiprocessor.core.parser.Response as ParserResponse

/**
 * openapi-parser Operation abstraction.
 */
class Operation(
    private val method: HttpMethod,
    private val operation: Operation31,
    private val path: Path31,
): ParserOperation {

    override fun getMethod(): HttpMethod = method

    override fun getOperationId(): String? {
        return operation.operationId
    }

    override fun getParameters(): List<ParserParameter> {
        val parameters = mutableListOf<ParserParameter>()

        path.parameters.map { p: Parameter31 ->
            var param = p
            if(p.isRef) {
                param = p.refObject
            }

            parameters.add(Parameter(param))
        }

        operation.parameters.map { p: Parameter31 ->
            var param = p
            if(p.isRef) {
                param = p.refObject
            }

            parameters.add(Parameter(param))
        }

        return parameters
    }

    override fun getRequestBody(): ParserRequestBody? {
        if (operation.requestBody == null) {
            return null
        }

        return RequestBody (operation.requestBody!!)
    }

    override fun getResponses(): Map<String, ParserResponse> {
        val content = linkedMapOf<String, ParserResponse>()

        operation.responses?.responses?.forEach { (key: String, value: Response31) ->
            content[key] = Response(value)
        }

        return content
    }

    override fun isDeprecated(): Boolean = operation.deprecated ?: false

    override fun hasTags(): Boolean = operation.tags?.isNotEmpty() ?: false

    override val summary: String? = operation.summary

    override val description: String? = operation.description

    override fun getFirstTag(): String? = operation.tags.first ()

}
