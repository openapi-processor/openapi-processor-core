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
        return BeanValidationInfoSimple(dataType, emptyList())
    }

    fun validate(dataType: DataType, required: Boolean = false): BeanValidationInfo {
        return if (dataType is CollectionDataType) {
            BeanValidationInfoCollection(
                dataType,
                collectAnnotations(dataType, required),
                validate(dataType.item, false)
            )
        } else {
            BeanValidationInfoSimple(
                dataType,
                collectAnnotations(dataType, required)
            )
        }
    }

    private fun collectAnnotations(dataType: DataType, required: Boolean = false): List<Annotation>  {
        val annotations = mutableListOf<Annotation>()

        if (dataType.isModel() || dataType.isInterface() || dataType.isArrayOfModel()) {
            annotations.add(Annotation(BeanValidation.VALID.typeName))
        }

        if (required) {
            annotations.add(Annotation(BeanValidation.NOT_NULL.typeName))
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

        if (dataType.emailConstraint()) {
            annotations.add(createEmailAnnotation())
        }

        return annotations
    }

    private fun createDecimalMinAnnotation(dataType: DataType): Annotation {
        val parameters = linkedMapOf<String, String>()

        val minimum = dataType.constraints?.minimumConstraint!!
        parameters["value"] = """"${minimum.value}""""

        if (minimum.exclusive) {
            parameters["inclusive"] = "false"
        }

        return Annotation(BeanValidation.DECIMAL_MIN.typeName, parameters)
    }

    private fun createDecimalMaxAnnotation(dataType: DataType): Annotation {
        val parameters = linkedMapOf<String, String>()

        val maximum = dataType.constraints?.maximumConstraint!!
        parameters["value"] = """"${maximum.value}""""

        if (maximum.exclusive) {
            parameters["inclusive"] = "false"
        }

        return Annotation(BeanValidation.DECIMAL_MAX.typeName, parameters)
    }

    private fun createSizeAnnotation(dataType: DataType): Annotation {
        return if (dataType.isString()) {
            createSizeAnnotation(dataType.lengthConstraints())
        } else {
            createSizeAnnotation(dataType.itemConstraints())
        }
    }

    private fun createSizeAnnotation(size: SizeConstraints): Annotation {
        val parameters = linkedMapOf<String, String>()

        if (size.hasMin) {
            parameters["min"] = "${size.min}"
        }

        if (size.hasMax) {
            parameters["max"] = "${size.max}"
        }

        return Annotation(BeanValidation.SIZE.typeName, parameters)
    }

    private fun createPatternAnnotation(dataType: DataType): Annotation {
        val parameters = linkedMapOf<String, String>()
        parameters["regexp"] = """"${escapeJava(dataType.constraints?.pattern!!)}""""
        return Annotation(BeanValidation.PATTERN.typeName, parameters)
    }

    private fun createEmailAnnotation(): Annotation {
        return Annotation(BeanValidation.EMAIL.typeName)
    }
}

private fun DataType.isModel(): Boolean = this is ModelDataType

private fun DataType.isInterface(): Boolean = this is InterfaceDataType

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

private fun DataType.emailConstraint(): Boolean = "email" == constraints?.format
