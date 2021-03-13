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
     * The Java type name without package.
     *
     * @return the type name.
     */
    fun getName(): String

    /**
     * The package of this type without class.
     */
    fun getPackageName(): String

    /**
     * Provides the import(s) of this type, usually a single import. If it is a generic type it will
     * add another import for each generic parameter. Used to create the import of this data type.
     *
     * @return import of this type.
     */
    fun getImports(): Set<String>

    /**
     * Provides the list of imports for the types referenced by this this type. Used to create the
     * imports used by the data type itself.
     *
     * @return the referenced import list.
     */
    fun getReferencedImports(): Set<String>

    /**
     * Provides the constraint information of the data type.
     *
     * @return the constraints or null if there are no constraints.
     */
    fun getConstraints(): DataTypeConstraints? {
        return null
    }

    /**
     * is the data type deprecated?
     *
     * @return true if deprecated, else false
     */
    fun isDeprecated(): Boolean {
        return false
    }

    val documentation: Documentation?

}
