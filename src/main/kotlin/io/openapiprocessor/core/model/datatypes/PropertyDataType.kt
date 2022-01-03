/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.model.datatypes

import io.openapiprocessor.core.model.Documentation

/**
 * schema "properties" data type wrapper. readOnly/writeOnly may be different on each use of the
 * same schema as a property in another schema.
 */
open class PropertyDataType(
    val readOnly: Boolean,
    val writeOnly: Boolean,
    val dataType: DataType
): DataType {

    override fun getName(): String {
        return dataType.getName()
    }

    override fun getTypeName(): String {
        return dataType.getTypeName()
    }

    override fun getPackageName(): String {
        return dataType.getPackageName()
    }

    override fun getImports(): Set<String> {
        return dataType.getImports()
    }

    override val referencedImports: Set<String>
        get() = dataType.referencedImports

    override val constraints: DataTypeConstraints?
        get() = dataType.constraints

    override val deprecated: Boolean
        get() = dataType.deprecated

    override val documentation: Documentation?
        get() = dataType.documentation

}

