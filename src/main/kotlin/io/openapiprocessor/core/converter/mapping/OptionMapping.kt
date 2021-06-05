/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping

/**
 * allows to handle simple name/value options in the mappings configuration.
 */
class OptionMapping<T>(val name: String, val value: T): Mapping {

    override fun getChildMappings(): List<Mapping> {
        return listOf(this)
    }

}
