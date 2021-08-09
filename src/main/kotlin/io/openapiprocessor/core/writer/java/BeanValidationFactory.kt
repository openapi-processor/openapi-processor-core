/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import io.openapiprocessor.core.model.datatypes.*
import org.apache.commons.text.StringEscapeUtils.escapeJava

/**
 * creates bean validation imports and annotations.
 */
open class BeanValidationFactory {

    /**
     * override to add annotations to the model object class.
     */
    open fun validate(dataType: ModelDataType): BeanValidationInfo {
        return BeanValidationInfoSimple(dataType, emptySet(), emptyList())
    }

    fun validate(dataType: DataType, required: Boolean = false): BeanValidationInfo {
        return if (dataType is CollectionDataType) {
            BeanValidationInfoCollection(
                dataType,
                collectImports(dataType, required),
                collectAnnotations(dataType, required),
                validate(dataType.item, false)
            )
        } else {
            BeanValidationInfoSimple(
                dataType,
                collectImports(dataType, required),
                collectAnnotations(dataType, required)
            )
        }
    }

    private fun collectImports(dataType: DataType, required: Boolean = false): Set<String> {
        val imports = mutableSetOf<String>()

        if (requiresValidImport(dataType)) {
            imports.add(BeanValidation.VALID.import)
        }

        if (required) {
            imports.add(BeanValidation.NOT_NULL.import)
        }

        if (dataType.hasSizeConstraints()) {
            imports.add(BeanValidation.SIZE.import)
        }

        if (dataType.hasMinConstraint()) {
            imports.add(BeanValidation.DECIMAL_MIN.import)
        }

        if (dataType.hasMaxConstraint()) {
            imports.add(BeanValidation.DECIMAL_MAX.import)
        }

        if (dataType.patternConstraint()) {
            imports.add(BeanValidation.PATTERN.import)
        }

        return imports
    }

    // uses list to keep order
    private fun collectAnnotations(dataType: DataType, required: Boolean = false): List<String> {
        val annotations = mutableListOf<String>()

        if (dataType.isModel() || dataType.isArrayOfModel()) {
            annotations.add(BeanValidation.VALID.annotation)
        }

        if (required) {
            annotations.add(BeanValidation.NOT_NULL.annotation)
        }

        if (dataType.hasSizeConstraints()) {
            annotations.add(createSizeAnnotation(dataType))
        }

        if (dataType.hasMinConstraint()) {
            annotations.add(createDecimalMinAnnotation (dataType))
        }

        if (dataType.hasMaxConstraint()) {
            annotations.add(createDecimalMaxAnnotation (dataType))
        }

        if (dataType.patternConstraint()) {
            annotations.add(createPatternAnnotation(dataType))
        }

        return annotations
    }

    private fun requiresValidImport(dataType: DataType): Boolean {
        if (dataType.isModel())
            return true

        if (dataType !is CollectionDataType)
            return false

        val itemDataType = dataType.item
        if (!itemDataType.isModel())
            return false

        return true
    }

    private fun createDecimalMinAnnotation(dataType: DataType): String {
        val minimum = dataType.constraints?.minimumConstraint!!
        return if(minimum.exclusive) {
            "@DecimalMin(value = \"${minimum.value}\", inclusive = false)"
        } else {
            "@DecimalMin(value = \"${minimum.value}\")"
        }
    }

    private fun createDecimalMaxAnnotation(dataType: DataType): String {
        val maximum = dataType.constraints?.maximumConstraint!!
        return if(maximum.exclusive) {
            "@DecimalMax(value = \"${maximum.value}\", inclusive = false)"
        } else {
            "@DecimalMax(value = \"${maximum.value}\")"
        }
    }

    private fun createSizeAnnotation(dataType: DataType): String {
        return if (dataType.isString()) {
            createSizeAnnotation(dataType.lengthConstraints())
        } else {
            createSizeAnnotation(dataType.itemConstraints())
        }
    }

    private fun createSizeAnnotation(size: SizeConstraints): String {
        return when {
            size.hasMin && size.hasMax -> {
                "@Size(min = ${size.min}, max = ${size.max})"
            }
            size.hasMin -> {
                "@Size(min = ${size.min})"
            }
            else -> {
                "@Size(max = ${size.max})"
            }
        }
    }

    private fun createPatternAnnotation(dataType: DataType): String {
        return """@Pattern(regexp = "${escapeJava(dataType.constraints?.pattern!!)}")"""
    }
}

private fun DataType.isModel(): Boolean = this is ModelDataType

private fun DataType.isArrayOfModel(): Boolean {
    if (this !is ArrayDataType)
        return false

    return item.isModel()
}

private fun DataType.isString(): Boolean = this is StringDataType

private fun DataType.isCollection(): Boolean =
      this is ArrayDataType
   || this is MappedCollectionDataType

private fun DataType.isNumber(): Boolean =
      this is FloatDataType
   || this is DoubleDataType
   || this is IntegerDataType
   || this is LongDataType

private fun DataType.hasArrayConstraints(): Boolean = constraints?.hasItemConstraints() ?: false

private fun DataType.hasLengthConstraints(): Boolean = constraints?.hasLengthConstraints() ?: false

private fun DataType.hasSizeConstraints(): Boolean =
       (isCollection() && hasArrayConstraints())
    || (isString() && hasLengthConstraints())

private fun DataType.hasMinConstraint(): Boolean = isNumber() && constraints?.minimum != null

private fun DataType.hasMaxConstraint(): Boolean = isNumber() && constraints?.maximum != null

private fun DataType.lengthConstraints(): SizeConstraints = constraints?.lengthConstraints!!

private fun DataType.itemConstraints(): SizeConstraints = constraints?.itemConstraints!!

private fun DataType.patternConstraint(): Boolean = constraints?.pattern != null
