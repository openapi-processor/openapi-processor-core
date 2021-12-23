/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.model.datatypes

import io.openapiprocessor.core.model.Documentation

/**
 * Data type description of a Java data type.
 */
interface DataType {

    /**
     * the identifier used to reference this datatype. This is usually the name from the openapi
     * description. If it is an inline type it is a generated name.
     *
     * @return the name id.
     */
    fun getName(): String

    /**
     * The type name without "package". Often identical to [getName] except when it is not. Used
     * to modify the *output* name of the datatype.
     *
     * @return the type name.
     */
    fun getTypeName(): String {
        return getName()
    }

    /**
     * The "package" of this type without [getTypeName].
     */
    fun getPackageName(): String

    /**
     * Provides the import(s) of this type, usually a single import. If it is a generic type it
     * should add another import for each generic parameter. Used to create the import list of
     * this data type.
     *
     * @return import of this type.
     */
    fun getImports(): Set<String>

    /**
     * provides the list of imports for the types referenced by this this type. Used to create the
     * imports used by the data type itself.
     */
    val referencedImports: Set<String>
        get() = emptySet()

    /**
     * provides the constraint information of the data type.
     */
    val constraints: DataTypeConstraints?
        get() = null

    /**
     * is this data type deprecated?
     */
    val deprecated: Boolean
        get() = false

    /**
     * provides the documentation of the data type.
     */
    val documentation: Documentation?
        get() = null

}
