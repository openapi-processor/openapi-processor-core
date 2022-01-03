/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.support.datatypes

import io.openapiprocessor.core.model.Documentation
import io.openapiprocessor.core.model.datatypes.DataType
import io.openapiprocessor.core.model.datatypes.DataTypeConstraints
import io.openapiprocessor.core.model.datatypes.DataTypeName
import io.openapiprocessor.core.model.datatypes.ObjectDataType
import io.openapiprocessor.core.model.datatypes.PropertyDataType

class Builder {

    static PropertyDataType propertyDataType(
        DataType dataType,
        Boolean readOnly = false,
        Boolean writeOnly = false
    ) {
        return new PropertyDataType(readOnly, writeOnly, dataType)
    }

    static ObjectDataType objectDataType (
        String nameId,
        String pkg,
        LinkedHashMap<String, DataType> properties = [:],
        DataTypeConstraints constraints = null,
        Boolean deprecated = false,
        Documentation documentation = null
    ) {
        return new ObjectDataType(
            new DataTypeName(nameId, nameId),
            pkg,
            properties,
            constraints,
            deprecated,
            documentation
        )
    }

    static ObjectDataType objectDataType (
        DataTypeName dataTypeName,
        String pkg,
        LinkedHashMap<String, DataType> properties = [:],
        DataTypeConstraints constraints = null,
        Boolean deprecated = false,
        Documentation documentation = null
    ) {
        return new ObjectDataType(
            dataTypeName,
            pkg,
            properties,
            constraints,
            deprecated,
            documentation
        )
    }

    static ListDataType listDataType(
        DataType item,
        DataTypeConstraints constraints = null
    ) {
        return new ListDataType (item, constraints)
    }
}
