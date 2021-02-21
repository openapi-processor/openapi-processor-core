/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping

/**
 * Used with [EndpointTypeMapping] to configure the java type that should represent the schema
 * of the given endpoint parameter.
 */
class ParameterTypeMapping(

    /**
     * The parameter name of this mapping. Must match 1:1 with what is written in the api.
     */
    val parameterName: String,

    /**
     * Type mapping valid only for requests with parameter {@link #parameterName}.
     */
    val mapping: TypeMapping

): Mapping {

    @Override
    override fun getChildMappings(): List<Mapping> {
        return listOf(mapping)
    }

}
