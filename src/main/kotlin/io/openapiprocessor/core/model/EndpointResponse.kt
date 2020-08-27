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

import io.openapiprocessor.core.model.datatypes.ResultDataType

/**
 * The responses that can be returned by an endpoint method for one (successful) response.
 *
 * @author Martin Hauner
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
    val contentTypes: List<String>
        get() {
            val result = mutableListOf<String>()
            if (main.contentType != null) {
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
        if (main.responseType.isMultiOf()) {
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

    private fun getSingleResponseTypeName(): String = main.responseType.getName()

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
