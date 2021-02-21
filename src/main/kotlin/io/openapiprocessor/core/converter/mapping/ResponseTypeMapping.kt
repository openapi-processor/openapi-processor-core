/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping

/**
 * Used with [EndpointTypeMapping] to configure the java type that should represent the response
 * schema for the given endpoint content type.
 */
class ResponseTypeMapping(

    /**
     * The content type of this mapping. Must match 1:1 with what is written in the api.
     */
    val contentType: String,

    /**
     * Type mapping valid only for responses with {@link #contentType}.
     */
    val mapping: TypeMapping

): Mapping {

    override fun getChildMappings(): List<Mapping> {
        return listOf(mapping)
    }

}
