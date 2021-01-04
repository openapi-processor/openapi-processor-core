/*
 * Copyright 2020 the original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.openapiprocessor.core.parser.openapi4j

import io.openapiprocessor.core.model.HttpMethod
import io.openapiprocessor.core.parser.Operation as ParserOperation
import io.openapiprocessor.core.parser.Parameter as ParserParameter
import io.openapiprocessor.core.parser.RequestBody as ParserRequestBody
import io.openapiprocessor.core.parser.Response as ParserResponse
import org.openapi4j.parser.model.v3.Operation as O4jOperation
import org.openapi4j.parser.model.v3.Parameter as O4jParameter
import org.openapi4j.parser.model.v3.Response as O4jResponse

/**
 * openapi4j Operation abstraction.
 *
 * @author Martin Hauner
 */
class Operation(
    private val method: HttpMethod,
    private val operation: O4jOperation,
    private val refResolver: RefResolverNative,
): ParserOperation {

    override fun getMethod(): HttpMethod = method

    override fun getOperationId(): String? {
        return operation.operationId
    }

    override fun getParameters(): List<ParserParameter> {
        val parameters = mutableListOf<ParserParameter>()

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

    override fun hasTags(): Boolean = if (operation.tags != null) operation.tags.isNotEmpty() else false

    override val description: String?
        get() = operation.description

    override fun getFirstTag(): String? = operation.tags.first ()

}
