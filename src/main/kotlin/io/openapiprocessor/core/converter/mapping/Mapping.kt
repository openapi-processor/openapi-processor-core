/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping

/**
 * Common interface for type mappings.
 */
interface Mapping {

    /**
     * Returns the inner mappings.
     *
     * In case of an ENDPOINT mapping the IO or TYPE mappings.
     * In case of an IO mapping its parameter/response type mappings.
     * In case of a TYPE or RESULT the mapping itself.
     *
     * @return the inner type mappings.
     */
    fun getChildMappings (): List<Mapping>

}
