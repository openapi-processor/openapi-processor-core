/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import io.openapiprocessor.core.model.datatypes.*

data class BeanValidationValue(
    val dataTypeValue: String,
    val imports: Set<String>,
    val annotations: List<String>)

interface BeanValidationInfo {
    val dataType: DataType
    val imports: Set<String>
    val annotations: List<String>

    val prop: BeanValidationValue
    val inout: BeanValidationValue
}

class BeanValidationInfoSimple(
    override val dataType: DataType,
    override val imports: Set<String>,
    override val annotations: List<String>
): BeanValidationInfo {

    override val prop: BeanValidationValue
        get() {
            return BeanValidationValue(dataType.getTypeName(), imports, annotations)
        }

    override val inout: BeanValidationValue
        get() {
            val dt = mutableListOf<String>()
            dt.addAll(annotations)
            dt.add(dataType.getTypeName())
            return BeanValidationValue(dt.joinToString(" "), imports, emptyList())
        }
}

class BeanValidationInfoCollection(
    override val dataType: DataType,
    override val imports: Set<String>,
    override val annotations: List<String>,
    val item: BeanValidationInfo
): BeanValidationInfo {

    override val prop: BeanValidationValue
        get() {
            dataType as CollectionDataType

            return if (item.dataType is ModelDataType) {
                val allImports = mutableSetOf<String>()
                allImports.addAll(imports)
                if (dataType !is ArrayDataType) {
                    allImports.add(BeanValidation.VALID.import)
                }

                val colAnnotations = mutableListOf<String>()
                colAnnotations.addAll(annotations)

                val itemAnnotations = mutableSetOf<String>()
                if (dataType !is ArrayDataType) {
                    itemAnnotations.add(BeanValidation.VALID.annotation)
                }

                BeanValidationValue(
                    dataType.getTypeName(emptySet(), itemAnnotations),
                    allImports,
                    colAnnotations
                )
            } else {
                val allImports = mutableSetOf<String>()
                allImports.addAll(imports)
                if (dataType !is ArrayDataType) {
                    allImports.addAll(item.imports)
                }

                val colAnnotations = mutableListOf<String>()
                colAnnotations.addAll(annotations)

                val itemAnnotations = mutableSetOf<String>()
                if (dataType !is ArrayDataType) {
                    itemAnnotations.addAll(item.annotations)
                }

                BeanValidationValue(
                    dataType.getTypeName(emptySet(), itemAnnotations),
                    allImports,
                    colAnnotations
                )
            }
        }

    override val inout: BeanValidationValue
        get() {
            val cdt = dataType as CollectionDataType

            return if (item.dataType is ModelDataType) {
                val allImports = mutableSetOf<String>()
                allImports.addAll(imports)
                if (dataType !is ArrayDataType) {
                    allImports.add(BeanValidation.VALID.import)
                }

                val colAnnotations = mutableSetOf<String>()
                colAnnotations.addAll(annotations)

                val itemAnnotations = mutableSetOf<String>()
                if (dataType !is ArrayDataType) {
                    itemAnnotations.add(BeanValidation.VALID.annotation)
                }

                BeanValidationValue(
                    cdt.getTypeName(colAnnotations, itemAnnotations),
                    allImports,
                    emptyList())
            } else {
                val allImports = mutableSetOf<String>()
                allImports.addAll(imports)
                if (dataType !is ArrayDataType) {
                    allImports.addAll(item.imports)
                }

                val colAnnotations = mutableSetOf<String>()
                colAnnotations.addAll(annotations)

                val itemAnnotations = mutableSetOf<String>()
                if (dataType !is ArrayDataType) {
                    itemAnnotations.addAll(item.annotations)
                }

                BeanValidationValue(
                    cdt.getTypeName(colAnnotations, itemAnnotations),
                    allImports,
                    emptyList())
            }
        }

}
