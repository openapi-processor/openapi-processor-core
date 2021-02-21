/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping

/**
 * Used with [EndpointTypeMapping] to configure an additional endpoint parameter that is not
 * defined in the api description.
 */
class AddParameterTypeMapping(

    /**
     * The parameter name of this mapping.
     */
    val parameterName: String,

    /**
     * additional parameter type mapping.
     */
    val mapping: TypeMapping,

    /**
     * additional annotation of parameter.
     */
    val annotation: Annotation? = null

): Mapping {

    override fun getChildMappings(): List<Mapping> {
        return listOf(mapping)
    }

}
