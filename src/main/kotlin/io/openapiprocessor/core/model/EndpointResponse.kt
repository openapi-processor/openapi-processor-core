/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.model

import io.openapiprocessor.core.model.datatypes.AnyOneOfObjectDataType
import io.openapiprocessor.core.model.datatypes.ResultDataType
import io.openapiprocessor.core.processor.mapping.v2.ResultStyle

/**
 * The responses that can be returned by an endpoint method for one (successful) response.
 */
class EndpointResponse(

    /**
     * success response
     */
    private val main: Response,

    /**
     * additional (error) responses
     */
    private val errors: Set<Response>

) {

    val contentType: String
        get() = main.contentType

    /**
     * provides the response type based on the requested style.
     *
     * [ResultStyle.SUCCESS]
     * - response type is the single success response type regardless of the number of available
     * error responses.
     *
     * [ResultStyle.ALL]
     * - If the endpoint has multiple responses the response type is `Object`. If the response is
     * wrapped by a result data type (i.e. wrapper) the response type is `ResultDataType<?>`.
     * - If the endpoint has only a success response type it is used as the response type.
     *
     * @param style required style
     * @return the response data type in the requested style
     */
    fun getResponseType(style: ResultStyle): String {
        if (isAnyOneOfResponse())
            return getMultiResponseTypeName()

        if (style == ResultStyle.ALL && errors.isNotEmpty())
            return getMultiResponseTypeName()

        return getSingleResponseTypeName()
    }

    /**
     * test only: provides the response type.
     */
    val responseType: String
        get() {
            return getResponseType(ResultStyle.SUCCESS)
        }

    val description: String?
    get() = main.description

    /**
     * provides the imports required for {@link #getResponseType()}.
     *
     * @param style required style
     * @return list of imports
     */

    fun getResponseImports(style: ResultStyle): Set<String> {
        if (isAnyOneOfResponse())
            return getImportsMulti()

        if (style == ResultStyle.ALL && errors.isNotEmpty())
            return getImportsMulti()

        return getImportsSingle()
    }

    /**
     * returns a list with all content types.
     */
    val contentTypes: Set<String>
        get() {
            val result = mutableSetOf<String>()
            if (!main.empty) {
                result.add(main.contentType)
            }

            errors.forEach {
                result.add(it.contentType)
            }
            return result
        }

    private fun isAnyOneOfResponse(): Boolean {
        return main.responseType is AnyOneOfObjectDataType
    }

    /**
     * Object or ResultDataType<?> if wrapped
     */
    private fun getMultiResponseTypeName(): String {
        val rt = main.responseType
        if (rt is ResultDataType) {
            return rt.getNameMulti()
        }
        return "Object"
    }

    private fun getSingleResponseTypeName(): String = main.responseType.getTypeName()

    private fun getImportsMulti(): Set<String> {
        val rt = main.responseType
        return if (rt is ResultDataType) {
            rt.getImportsMulti()
        } else {
            emptySet()
        }
    }

    private fun getImportsSingle(): Set<String> {
        return main.imports
    }

}
