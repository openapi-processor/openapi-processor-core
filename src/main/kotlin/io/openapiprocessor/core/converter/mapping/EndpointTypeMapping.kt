/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping

/**
 * Used with {@link com.github.hauner.openapi.spring.converter.ApiOptions} to override parameter or
 * response type mappings on a single endpoint. It can also be used to add parameters that are not
 * defined in the api. For example to pass {@code javax.servlet.http.HttpServletRequest} to the
 * controller method.
 *
 * The {@code mappings} list can contain objects of the type
 * - {@link ParameterTypeMapping}
 * - {@link ResponseTypeMapping}
 */
class EndpointTypeMapping @JvmOverloads constructor(

    /**
     * Full path of the endpoint that is configured by this object.
     */
    var path: String,

    /**
     * Provides type mappings for the endpoint.
     */
    var typeMappings: List<Mapping> = emptyList(),

    /**
     * Exclude endpoint.
     */
    var exclude: Boolean = false

): Mapping {

    override fun getChildMappings(): List<Mapping> {
        return typeMappings
    }

}
