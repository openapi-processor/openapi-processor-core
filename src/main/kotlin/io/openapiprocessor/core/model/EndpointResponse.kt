/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.model

import io.openapiprocessor.core.model.datatypes.AnyOneOfObjectDataType
import io.openapiprocessor.core.model.datatypes.ResultDataType

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
     * provides the response type.
     *
     * If the endpoint has multiple responses and there is no result data type the response type
     * is `Object`. If the response has a result data type the response type is `ResultDataType<?>`.
     */
    val responseType: String
        get() {
            return if (hasMultipleResponses()) {
                getMultiResponseTypeName()
            } else {
                getSingleResponseTypeName()
            }
        }

    val description: String?
    get() = main.description

    /**
     * provides the imports required for {@link #getResponseType()}.
     *
     * @return list of imports
     */
    val responseImports: Set<String>
        get() {
            return if (hasMultipleResponses()) {
                getImportsMulti()
            } else {
                getImportsSingle()
            }
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

    /**
     * if this response has multiple types.
     */
    private fun hasMultipleResponses(): Boolean {
        if (main.responseType is AnyOneOfObjectDataType) {
            return true
        }
        return errors.isNotEmpty()
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
        val imports = mutableSetOf<String>()

        imports.addAll (main.imports)
        errors.forEach {
            imports.addAll (it.imports)
        }

        return imports
    }

}
