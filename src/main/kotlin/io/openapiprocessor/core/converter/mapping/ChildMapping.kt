/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping

interface ChildMapping {

    /**
     * Returns the nested mappings.
     *
     * In case of an endpoint mapping the io or type mappings.
     * In case of an io mapping its parameter/response type mappings.
     *
     * @return the nested type mappings.
     */
    fun getChildMappings (): List<Mapping>

}
