/*
 * Copyright © 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser.swagger

import io.openapiprocessor.core.parser.Parameter as ParserParameter
import io.openapiprocessor.core.parser.Schema as ParserSchema
import io.swagger.v3.oas.models.parameters.Parameter as SwaggerParameter

/**
 * Swagger Parameter abstraction.
 */
class Parameter(private val parameter: SwaggerParameter): ParserParameter {

    override fun getIn(): String = parameter.`in`

    override fun getName(): String = parameter.name

    override fun getSchema(): ParserSchema = Schema (parameter.schema)

    override fun isRequired(): Boolean = if(parameter.required != null) {
        parameter.required
    } else {
        false
    }

    override fun isDeprecated(): Boolean = parameter.deprecated ?: false

    override val description: String?
        get() = parameter.description

}
