/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.model.datatypes

interface CollectionDataType {
    val item: DataType

    fun getTypeName(annotations: Set<String>, itemAnnotations: Set<String>): String
}
