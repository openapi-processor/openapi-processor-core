/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.support.datatypes

import io.openapiprocessor.core.model.datatypes.DataTypeName as DataTypeNameKt
import org.jetbrains.annotations.NotNull

/**
 * easier construction from groovy
 */
class DataTypeName extends DataTypeNameKt {

    DataTypeName (@NotNull String id) {
        super (id, id)
    }

    DataTypeName (@NotNull String id, @NotNull String type) {
        super (id, type)
    }

}
