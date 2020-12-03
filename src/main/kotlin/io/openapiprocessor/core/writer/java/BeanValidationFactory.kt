/*
 * Copyright © 2019-2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import io.openapiprocessor.core.model.datatypes.*

/**
 * creates bean validation imports and annotations.
 */
open class BeanValidationFactory {

    fun collectImports(dataType: DataType, required: Boolean = false): Set<String> {
        val imports = mutableSetOf<String>()

        if (dataType.isObject()) {
            imports.add("javax.validation.Valid")
        }

        if (required) {
            imports.add("javax.validation.constraints.NotNull")
        }

        if (dataType.hasSizeConstraints()) {
            imports.add("javax.validation.constraints.Size")
        }

        if (dataType.hasMinConstraint()) {
            imports.add("javax.validation.constraints.DecimalMin")
        }

        if (dataType.hasMaxConstraint()) {
            imports.add("javax.validation.constraints.DecimalMax")
        }

        return imports
    }

    fun createAnnotations(dataType: DataType, required: Boolean = false): String {
        val annotations = mutableListOf<String>()

        if (dataType.isObject()) {
            annotations.add(createValidAnnotation())
        }

        if (required) {
            annotations.add(createNotNullAnnotation())
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

        return annotations.joinToString (" ")
    }

    private fun createValidAnnotation(): String = "@Valid"

    private fun createNotNullAnnotation(): String = "@NotNull"

    private fun createDecimalMinAnnotation(dataType: DataType): String {
        val minimum = dataType.getConstraints()?.minimumConstraint!!
        return if(minimum.exclusive) {
            "@DecimalMin(value = \"${minimum.value}\", inclusive = false)"
        } else {
            "@DecimalMin(value = \"${minimum.value}\")"
        }
    }

    private fun createDecimalMaxAnnotation(dataType: DataType): String {
        val maximum = dataType.getConstraints()?.maximumConstraint!!
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

}

private fun DataType.isObject(): Boolean = this is ObjectDataType

private fun DataType.isString(): Boolean = this is StringDataType

private fun DataType.isCollection(): Boolean =
      this is ArrayDataType
   || this is MappedCollectionDataType
   || this is MappedMapDataType

private fun DataType.isNumber(): Boolean =
      this is FloatDataType
   || this is DoubleDataType
   || this is IntegerDataType
   || this is LongDataType

private fun DataType.hasNotNullableConstraint(): Boolean = !(getConstraints()?.nullable ?: false)

private fun DataType.hasArrayConstraints(): Boolean = getConstraints()?.hasItemConstraints() ?: false

private fun DataType.hasLengthConstraints(): Boolean = getConstraints()?.hasLengthConstraints() ?: false

private fun DataType.hasSizeConstraints(): Boolean =
       (isCollection() && hasArrayConstraints())
    || (isString() && hasLengthConstraints())

private fun DataType.hasMinConstraint(): Boolean = isNumber() && getConstraints()?.minimum != null

private fun DataType.hasMaxConstraint(): Boolean = isNumber() && getConstraints()?.maximum != null

private fun DataType.lengthConstraints(): SizeConstraints = getConstraints()?.lengthConstraints!!

private fun DataType.itemConstraints(): SizeConstraints = getConstraints()?.itemConstraints!!
