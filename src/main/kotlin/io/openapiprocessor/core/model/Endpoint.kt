/*
 * Copyright 2019-2020 the original authors
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

package io.openapiprocessor.core.model


import io.openapiprocessor.core.model.parameters.MultipartParameter
import io.openapiprocessor.core.model.parameters.Parameter

/**
 * Endpoint properties.
 *
 * @author Martin Hauner
 */
class Endpoint(
    val path: String,
    val method: HttpMethod,
    val operationId: String?,
    val deprecated: Boolean
) {
//    String path
//    HttpMethod method
//    String operationId
//    boolean deprecated

    // todo
    /*private*/ val parameters: MutableList<Parameter> = mutableListOf() // todo not mutable
    /*private*/ val requestBodies: MutableList<RequestBody> = mutableListOf() // todo not mutable
    private /*val*/ var responses: MutableMap<String, List<Response>>  = mutableMapOf()

    // grouped responses
    lateinit var endpointResponses: List<EndpointResponse>// = emptyList()

    // todo
    fun addResponses(httpStatus: String, statusResponses: List<Response>) {
        responses[httpStatus] = statusResponses
    }

    // fluent
    fun initEndpointResponses (): Endpoint {
        endpointResponses = createEndpointResponses ()
        return this
    }

    // hmmm
    fun getRequestBody(): RequestBody {
        return requestBodies.first ()
    }

    /**
     * all possible responses for an openapi status ('200', '2xx',... or 'default'). If the given
     * status has no responses the result is an empty list.
     *
     * @param status the response status
     * @return the list of responses
     */
    private fun getResponses (status: String): List<Response> {
        if (!responses.containsKey (status)) {
            return emptyList()
        }
        return responses[status]!!
    }

    /**
     * provides a list of all consumed content types of this endpoint including multipart/form-data.
     *
     * multipart/form-data is special because a multipart request body with multiple properties is
     * converted to multiple {@link MultipartParameter}s in the internal model. The request body
     * information is no longer available.
     *
     * @return the list of content types
     */
    // only called by tests?
    fun getConsumesContentTypes(): List<String> {
        val contentTypes = requestBodies
            .map { it.contentType }
            .toSet()
            .toMutableList()

        val multipart = parameters.find {
            it is MultipartParameter
        }

        if (multipart != null) {
            contentTypes.add ("multipart/form-data")
        }

        return contentTypes
    }

    // not needed.... => EndpointResponse.getContentTypes()
    // only called by tests?
    fun getProducesContentTypes (status: String): List<String> {
        val responses = getResponses (status)
        val errors = getErrorResponses ()

        val contentTypes = mutableSetOf<String>()
        responses.forEach {
            if (it.empty) {
                return@forEach
            }

            contentTypes.add (it.contentType)
        }

        errors.forEach {
            contentTypes.add (it.contentType)
        }

        return contentTypes.toList ()
    }

    /**
     * test support => extension function ?
     *
     * @param status the response status
     * @return first response of status
     */
    fun getFirstResponse (status: String): Response? {
        if (!responses.containsKey (status)) {
            return null
        }

        val resp = responses[status]
        if (resp != null && resp.isEmpty()) {
            return null
        }

        return resp?.first ()
    }

    /**
     * checks if the endpoint has multiple success responses with different content types.
     *
     * @return true if condition is met, otherwise false.
     */
    fun hasMultipleEndpointResponses(): Boolean {
        return endpointResponses.size > 1
    }

    /**
     * creates groups from the responses.
     *
     * if the endpoint does provide its result in multiple content types it will create one entry
     * for each response kind (main response). if error responses are defined they are added as
     * error responses.
     *
     * this is used to create one controller method for each (successful) response definition.
     *
     * @return list of method responses
     */
    private fun createEndpointResponses(): List<EndpointResponse> {
        val successes = getSuccessResponses()
        val errors = getErrorResponses()
        return successes.map {
            EndpointResponse(it, errors)
        }
    }

    private fun getSuccessResponses(): Set<Response> {
        return responses
            .filterKeys { it.startsWith("2") }
            .values
            .flatten()
            .toSet()
    }

    private fun getErrorResponses(): Set<Response> {
        return responses
            .filterKeys { !it.startsWith("2") }
            .values
            .map { it.first() }
            .toSet()
    }

}
