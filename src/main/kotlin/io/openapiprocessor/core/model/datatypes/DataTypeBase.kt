/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.model.datatypes

/**
 * basic DataType implementation. Reduces duplication in DataTypes.
 */
abstract class DataTypeBase: DataType {

    override fun getImports(): Set<String> {
        return setOf(getPackageName() + "." + getName())
    }

    override fun getReferencedImports(): Set<String> {
        return emptySet()
    }

}
