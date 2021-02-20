/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping

/**
 * provider of target type information.
 */
interface TargetTypeMapping {

    fun getTargetType (): TargetType?

}

/**
 * "null" implementation off [TargetTypeMapping] that always returns null
 */
class NoTargetTypeMapping: TargetTypeMapping {

    override fun getTargetType(): TargetType? {
        return null
    }

}
