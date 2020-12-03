/*
 * Copyright © 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.model.datatypes

interface ModelDataType: DataType {

    fun isModel(): Boolean

    fun getProperties(): Map<String, DataType>

}
