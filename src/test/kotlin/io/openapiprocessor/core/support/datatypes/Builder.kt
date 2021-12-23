/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.support.datatypes

import io.openapiprocessor.core.model.datatypes.*

fun propertyDataType(dataType: DataType): PropertyDataType {
    return PropertyDataType(readOnly = false, writeOnly = false, dataType = dataType)
}

fun propertyDataTypeString(): PropertyDataType {
    return PropertyDataType(readOnly = false, writeOnly = false, dataType = StringDataType())
}
