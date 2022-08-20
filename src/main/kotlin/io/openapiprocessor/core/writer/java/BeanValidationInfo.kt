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
    val annotations: List<Annotation>

    val prop: BeanValidationValue
    val inout: BeanValidationValue
}

class BeanValidationInfoSimple(
    override val dataType: DataType,
    override val annotations: List<Annotation>
): BeanValidationInfo {

    override val prop: BeanValidationValue
        get() = BeanValidationValue(
            dataType.getTypeName(),
            annotationImports,
            annotationValues
        )

    override val inout: BeanValidationValue
        get() = BeanValidationValue(
            dataTypeWithAnnotations,
            annotationImports,
            emptyList()
        )

    private val annotationImports: Set<String>
        get() = annotations.map { it.import }.toSet()

    private val annotationValues: List<String>
        get() = annotations.map { buildAnnotation(it) }.toList()

    private val dataTypeWithAnnotations: String
        get() {
            val dt = mutableListOf<String>()
            dt.addAll(annotations.map { it.annotation })
            dt.add(dataType.getTypeName())
            return dt.joinToString(" ")
        }
}

class BeanValidationInfoCollection(
    override val dataType: DataType,
    override val annotations: List<Annotation>,
    val item: BeanValidationInfo
): BeanValidationInfo {

    override val prop: BeanValidationValue
        get() {
            dataType as CollectionDataType

            return if (item.dataType is ModelDataType) {
                val allImports = mutableSetOf<String>()
                allImports.addAll(annotationImports)
                allImports.addAll(itemAnnotationImports)

                val collectionAnnotations = mutableListOf<String>()
                collectionAnnotations.addAll(annotationValues)

                val itemAnnotations = mutableSetOf<String>()
                itemAnnotations.addAll(itemAnnotationValues)

                BeanValidationValue(
                    dataType.getTypeName(emptySet(), itemAnnotations),
                    allImports,
                    collectionAnnotations
                )
            } else {
                val allImports = mutableSetOf<String>()
                allImports.addAll(annotationImports)

                if (dataType !is ArrayDataType) {
                    allImports.addAll(itemAnnotationImports)
                }

                val collectionAnnotations = mutableListOf<String>()
                collectionAnnotations.addAll(annotationValues)

                val itemAnnotations = mutableSetOf<String>()
                if (dataType !is ArrayDataType) {
                    itemAnnotations.addAll(itemAnnotationValues)
                }

                BeanValidationValue(
                    dataType.getTypeName(emptySet(), itemAnnotations),
                    allImports,
                    collectionAnnotations
                )
            }
        }

    override val inout: BeanValidationValue
        get() {
            val cdt = dataType as CollectionDataType

            return if (item.dataType is ModelDataType) {
                val allImports = mutableSetOf<String>()
                allImports.addAll(annotationImports)
                allImports.addAll(itemAnnotationImports)

                val collectionAnnotations = mutableSetOf<String>()
                collectionAnnotations.addAll(annotationValues)

                val itemAnnotations = mutableSetOf<String>()
                itemAnnotations.addAll(itemAnnotationValues)

                BeanValidationValue(
                    cdt.getTypeName(collectionAnnotations, itemAnnotations),
                    allImports,
                    emptyList())
            } else {
                val allImports = mutableSetOf<String>()
                allImports.addAll(annotationImports)

                if (dataType !is ArrayDataType) {
                    allImports.addAll(itemAnnotationImports)
                }

                val colAnnotations = mutableSetOf<String>()
                colAnnotations.addAll(annotationValues)

                val itemAnnotations = mutableSetOf<String>()
                if (dataType !is ArrayDataType) {
                    itemAnnotations.addAll(itemAnnotationValues)
                }

                BeanValidationValue(
                    cdt.getTypeName(colAnnotations, itemAnnotations),
                    allImports,
                    emptyList())
            }
        }

    private val annotationImports: Set<String>
        get() = annotations.map { it.import }.toSet()

    private val annotationValues: List<String>
        get() = annotations.map { it.annotation }.toList()

    private val itemAnnotationImports: Set<String>
        get() = item.annotations.map { it.import }.toSet()

    private val itemAnnotationValues: List<String>
        get() = item.annotations.map { it.annotation }.toList()
}
