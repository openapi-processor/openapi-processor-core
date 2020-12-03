/*
 * Copyright © 2019-2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.model.datatypes

/**
 * OpenAPI constraint details of a data type.
 */
class DataTypeConstraints(

    var /*val*/ defaultValue: Any? = null, // todo rename to default
    var /*val*/ nullable: Boolean = false,
    var /*val*/ minLength: Int = 0,
    var /*val*/ maxLength: Int? = null,
    var /*val*/ minimum: Number? = null,
    var /*val*/ exclusiveMinimum: Boolean = false,
    var /*val*/ maximum: Number? = null,
    var /*val*/ exclusiveMaximum: Boolean = false,
    var /*val*/ minItems: Int = 0,
    var /*val*/ maxItems: Int? = null,
    var /*val*/ required: List<String> = emptyList()

) {

    fun getDefault(): Any? = defaultValue

    fun hasItemConstraints(): Boolean = minItems != 0 || maxItems != null

    val itemConstraints: SizeConstraints
        get() = SizeConstraints(minItems, maxItems)

    fun hasLengthConstraints(): Boolean = minLength != 0 || maxLength != null

    val lengthConstraints: SizeConstraints
        get() = SizeConstraints(minLength, maxLength)

    val minimumConstraint: NumberConstraint
        get() = NumberConstraint(minimum!!, exclusiveMinimum)

    val maximumConstraint: NumberConstraint
        get() = NumberConstraint(maximum!!, exclusiveMaximum)

    fun isRequired(prop: String): Boolean {
        return required.contains(prop);
    }

}

data class SizeConstraints(val min: Int, val max: Int?) {

    val hasMin = min > 0
    val hasMax = max != null

}

data class NumberConstraint(val value: Number, val exclusive: Boolean)
