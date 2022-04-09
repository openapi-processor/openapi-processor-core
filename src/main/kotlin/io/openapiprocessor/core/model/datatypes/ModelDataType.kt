/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.model.datatypes

interface ModelDataType: DataType {
    /**
     * implements this interface.
     */
    var implementsDataType: InterfaceDataType?

    /**
     * loop object properties.
     */
    fun forEach(action: (property: String, dataType: DataType) -> Unit)

    fun isRequired(prop: String): Boolean

}
