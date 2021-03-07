/*
 * Copyright © 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser.openapi4j

import io.openapiprocessor.core.model.HttpMethod
import io.openapiprocessor.core.parser.Operation as ParserOperation
import io.openapiprocessor.core.parser.Parameter as ParserParameter
import io.openapiprocessor.core.parser.RequestBody as ParserRequestBody
import io.openapiprocessor.core.parser.Response as ParserResponse
import org.openapi4j.parser.model.v3.Operation as O4jOperation
import org.openapi4j.parser.model.v3.Parameter as O4jParameter
import org.openapi4j.parser.model.v3.Path as O4jPath
import org.openapi4j.parser.model.v3.Response as O4jResponse

/**
 * openapi4j Operation abstraction.
 */
class Operation(
    private val method: HttpMethod,
    private val operation: O4jOperation,
    private val path: O4jPath,
    private val refResolver: RefResolverNative
): ParserOperation {

    override fun getMethod(): HttpMethod = method

    override fun getOperationId(): String? {
        return operation.operationId
    }

    override fun getParameters(): List<ParserParameter> {
        val parameters = mutableListOf<ParserParameter>()

        path.parameters?.map { p: O4jParameter ->
            var param = p
            if(p.isRef) {
                param = refResolver.resolve(p)
            }

            parameters.add(Parameter(param))
        }

        operation.parameters?.map { p: O4jParameter ->
            var param = p
            if(p.isRef) {
                param = refResolver.resolve(p)
            }

            parameters.add(Parameter(param))
        }

        return parameters
    }

    override fun getRequestBody(): ParserRequestBody? {
        if (operation.requestBody == null) {
            return null
        }

        return RequestBody (operation.requestBody)
    }

    override fun getResponses(): Map<String, ParserResponse> {
        val content = linkedMapOf<String, ParserResponse>()

        operation.responses.forEach { (key: String, value: O4jResponse) ->
            content.put (key, Response(value))
        }

        return content
    }

    override fun isDeprecated(): Boolean = operation.deprecated ?: false

    override fun hasTags(): Boolean = operation.tags?.isNotEmpty() ?: false

    override val summary: String? = operation.summary

    override val description: String? = operation.description

    override fun getFirstTag(): String? = operation.tags.first ()

}
