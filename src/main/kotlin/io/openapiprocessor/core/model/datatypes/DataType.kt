/*
 * Copyright 2019-2020 the original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.openapiprocessor.core.model.datatypes

/**
 * Data type description of a Java data type.
 *
 * @author Martin Hauner
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

    /**
     * is this a composed type, i.e. allOf, anyOf, oneOf ?
     */
    fun isComposed(): Boolean {
        return false
    }

}
