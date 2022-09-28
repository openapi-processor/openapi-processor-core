/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

/**
 * Removes (unnecessary) imports from a given list of imports.
 */
class DefaultImportFilter: ImportFilter {

    /**
     * removes (unnecessary) imports from the given list of imports. It removes classes from the
     * same package and from java.lang.
     *
     * @param currentPackageName the package of the current class
     * @param imports list of imports
     * @return list of imports without the unnecessary import
     */
    override fun filter(currentPackageName: String, imports: Set<String>): Set<String> {
        return imports.filterNot {
               it.startsWith("java.lang.")
            || it.substringBeforeLast(".", it) == currentPackageName
        }.toSet()
    }

}
