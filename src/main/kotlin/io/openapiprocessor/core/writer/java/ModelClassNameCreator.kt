/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import io.openapiprocessor.core.support.capitalizeFirstChar

/**
 * create a class name, possibly adding a suffix if any is configured.
 */
class ModelClassNameCreator(private val suffix: String) {

    fun createName(dataTypeName: String): String {
        if (dataTypeName.isEmpty())
            return dataTypeName

        if (dataTypeName.endsWith(suffix.capitalizeFirstChar()))
            return dataTypeName

        return "$dataTypeName${suffix.capitalizeFirstChar()}"
    }

}
