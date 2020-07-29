/*
 * Copyright 2020 the original authors
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

package io.openapiprocessor.core.writer.java

/**
 * Removes (unnecessary) imports from a given list of imports.
 *
 * @author Martin Hauner
 */
class DefaultImportFilter: ImportFilter {

    /**
     * removes (unnecessary) imports from the given list of imports. It removes classes from the
     * same package and from from java.lang.
     *
     * @param currentPackageName the package of the current class
     * @param imports list of imports
     * @return list of imports without the unnecessary import
     */
    override fun filter(currentPackageName: String, imports: Set<String>): Set<String> {
        return imports.filterNot {
            it.startsWith("java.lang.") || it.startsWith(currentPackageName)
        }.toSet()
    }

}
