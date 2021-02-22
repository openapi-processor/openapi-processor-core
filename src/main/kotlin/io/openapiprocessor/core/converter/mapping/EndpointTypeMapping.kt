/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping

import io.openapiprocessor.core.model.HttpMethod

/**
 * Used with [io.openapiprocessor.core.converter.ApiOptions] to store endpoint specific type
 * mappings. It can also be used to add parameters that are not defined in the api. For example
 * to pass a [javax.servlet.http.HttpServletRequest] to the*controller method.
 */
class EndpointTypeMapping @JvmOverloads constructor(

    /**
     * full path of the endpoint that is configured by this object.
     */
    val path: String,

    /**
     * http method of this endpoint. If it is not set (i.e null) the mapping applies to all http
     * methods.
     */
    val method: HttpMethod? = null,

    /**
     * provides type mappings for the endpoint.
     */
    val typeMappings: List<Mapping> = emptyList(),

    /**
     * exclude endpoint.
     */
    val exclude: Boolean = false

): Mapping {

    override fun getChildMappings(): List<Mapping> {
        return typeMappings
    }

}
