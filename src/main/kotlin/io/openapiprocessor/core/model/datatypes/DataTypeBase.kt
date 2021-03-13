/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.model.datatypes

import io.openapiprocessor.core.model.Documentation

/**
 * basic DataType implementation. Reduces duplication in DataTypes.
 */
abstract class DataTypeBase(

    private val constraints: DataTypeConstraints? = null,
    private val deprecated: Boolean = false,
    override val documentation: Documentation? = null

): DataType {

    override fun getImports(): Set<String> {
        return setOf(getPackageName() + "." + getName())
    }

    override fun getReferencedImports(): Set<String> {
        return emptySet()
    }

    override fun getConstraints(): DataTypeConstraints? {
        return constraints
    }

    override fun isDeprecated(): Boolean {
        return deprecated
    }

}
