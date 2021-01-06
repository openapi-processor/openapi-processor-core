/*
 * Copyright © 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */
package io.openapiprocessor.core.builder.api

import io.openapiprocessor.core.model.RequestBody
import io.openapiprocessor.core.model.datatypes.DataType
import io.openapiprocessor.core.model.parameters.Parameter
import io.openapiprocessor.core.model.parameters.QueryParameter

class ParametersBuilder {
    private val parameters: MutableList<Parameter> = mutableListOf()
    private val bodies: MutableList<RequestBody> = mutableListOf()

    fun any(parameter: Parameter, init: ParameterBuilder.() -> Unit? = {}) {
        parameters.add(parameter)
    }

    fun query(name: String, dataType: DataType, init: ParameterBuilder.() -> Unit? = {}) {
        val builder = ParameterBuilder()
        init(builder)
        parameters.add(QueryParameter(
            name,
            dataType,
            builder.required,
            builder.deprecated,
            builder.description))
    }

    fun body(name: String, contentType: String, dataType: DataType,
             init: ParameterBuilder.() -> Unit = {}) {
        val builder = ParameterBuilder()
        init(builder)
        bodies.add(RequestBody(name, contentType, dataType, builder.required, builder.deprecated))
    }

    fun parameters(): List<Parameter> {
        return parameters
    }

    fun bodies(): List<RequestBody> {
        return bodies
    }

}
