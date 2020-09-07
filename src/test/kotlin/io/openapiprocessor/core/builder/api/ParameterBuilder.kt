/*
 * Copyright © 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.builder.api


//class ParameterData(val required: Boolean, deprecated: Boolean)


class ParameterBuilder {
    var required: Boolean = false
    var deprecated: Boolean = false


    fun required() {
        required = true
    }

    fun deprecated() {
        deprecated = true
    }

}




//import io.openapiprocessor.core.model.Response
//import io.openapiprocessor.core.model.datatypes.DataType
//import io.openapiprocessor.core.model.datatypes.NoneDataType
//import io.openapiprocessor.core.model.test.EmptyResponse
//
//class ResponseBuilder() {
//    private val responses: MutableList<Response> = mutableListOf()
//
//    fun response(contentType: String? = null, dataType: DataType? = null) {
//        lateinit var response: Response
//
//        if(contentType == null && dataType == null)
//            response = EmptyResponse()
//        else
//            response = Response(
//                contentType ?: "none",
//                dataType ?: NoneDataType())
//
//        responses.add(response)
//    }
//
//    fun build(): List<Response> {
//        return  responses
//    }
//
//}
//
