/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.support.datatypes

import io.openapiprocessor.core.model.Documentation
import io.openapiprocessor.core.model.datatypes.*
import io.openapiprocessor.core.model.datatypes.DataTypeName
import io.openapiprocessor.core.model.datatypes.ObjectDataType

/**
 * easier test migration
 */
class ObjectDataType(
    nameId: String,
    pkg: String,
    properties: LinkedHashMap<String, PropertyDataType> = linkedMapOf(),
    constraints: DataTypeConstraints? = null,
    deprecated: Boolean = false,
    documentation: Documentation? = null
): ObjectDataType(DataTypeName(nameId), pkg, properties, constraints, deprecated, documentation)
