/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import io.openapiprocessor.core.model.datatypes.DataType
import io.openapiprocessor.core.model.datatypes.MappedCollectionDataType
import io.openapiprocessor.core.model.datatypes.ModelDataType

interface BeanValidationInfo {
    val dataType: DataType
    val imports: Set<String>
    val annotations: List<String>

    val typeName: String
        get() {
            return dataType.getTypeName()
        }

    val hasAnnotations
        get() = annotations.isNotEmpty()

}

class BeanValidationInfoObject(
    override val dataType: DataType,
    override val imports: Set<String>,
    override val annotations: List<String>
): BeanValidationInfo


class BeanValidationInfoProperty(
    override val dataType: DataType,
    override val imports: Set<String>,
    override val annotations: List<String>
): BeanValidationInfo {

    override val typeName: String
        get() {
            // no collection or array
            if (dataType !is MappedCollectionDataType)
                return dataType.getTypeName()

            if (dataType.item !is ModelDataType)
                return dataType.getTypeName()

            return dataType.getTypeNameWithAnnotatedItem(BeanValidation.VALID.annotation)
        }

}
