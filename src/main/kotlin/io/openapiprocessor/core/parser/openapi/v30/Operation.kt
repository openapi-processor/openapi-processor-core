/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser.openapi.v30

import io.openapiprocessor.core.model.HttpMethod
import io.openapiparser.model.v30.Operation as Operation30
import io.openapiparser.model.v30.Parameter as Parameter30
import io.openapiparser.model.v30.PathItem as Path30
import io.openapiparser.model.v30.Response as Response30
import io.openapiprocessor.core.parser.Operation as ParserOperation
import io.openapiprocessor.core.parser.Parameter as ParserParameter
import io.openapiprocessor.core.parser.RequestBody as ParserRequestBody
import io.openapiprocessor.core.parser.Response as ParserResponse

/**
 * openapi-parser Operation abstraction.
 */
class Operation(
    private val method: HttpMethod,
    private val operation: Operation30,
    private val path: Path30,
): ParserOperation {

    override fun getMethod(): HttpMethod = method

    override fun getOperationId(): String? {
        return operation.operationId
    }

    override fun getParameters(): List<ParserParameter> {
        val parameters = mutableListOf<ParserParameter>()

        path.parameters.map { p: Parameter30 ->
            var param = p
            if(p.isRef) {
                param = p.refObject
            }

            parameters.add(Parameter(param))
        }

        operation.parameters.map { p: Parameter30 ->
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

        operation.responses.responses.forEach { (key: String, value: Response30) ->
            content[key] = Response(value)
        }

        return content
    }

    override fun isDeprecated(): Boolean = operation.deprecated

    override fun hasTags(): Boolean = operation.tags.isNotEmpty()

    override val summary: String? = operation.summary

    override val description: String? = operation.description

    override fun getFirstTag(): String? = if (hasTags()) operation.tags.first() else null
}
