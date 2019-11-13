/*
 * Copyright 2019 the original authors
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

package com.github.hauner.openapi.spring.model.datatypes

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
    String getName ()

    /**
     * The package of this type without class.
     */
    String getPackageName ()

    /**
     * Provides the import of this type.
     *
     * @return import of this type.
     */
    String getImports ()

    /**
     * Provides the list of imports for the types referenced by this this type.
     *
     * @return the referenced import list.
     */
    Set<String> getReferencedImports ()
}
