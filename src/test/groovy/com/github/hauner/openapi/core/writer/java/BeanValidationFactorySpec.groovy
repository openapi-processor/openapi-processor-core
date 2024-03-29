/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package com.github.hauner.openapi.core.writer.java

import io.openapiprocessor.core.model.Documentation
import io.openapiprocessor.core.model.datatypes.MappedCollectionDataType
import io.openapiprocessor.core.model.datatypes.NoneDataType
import io.openapiprocessor.core.model.datatypes.ArrayDataType
import io.openapiprocessor.core.model.datatypes.DataType
import io.openapiprocessor.core.model.datatypes.DataTypeConstraints
import io.openapiprocessor.core.model.datatypes.DoubleDataType
import io.openapiprocessor.core.model.datatypes.FloatDataType
import io.openapiprocessor.core.model.datatypes.IntegerDataType
import io.openapiprocessor.core.model.datatypes.LongDataType
import io.openapiprocessor.core.model.datatypes.StringDataType
import io.openapiprocessor.core.support.datatypes.ObjectDataType
import io.openapiprocessor.core.writer.java.AnnotationWriterKt
import io.openapiprocessor.core.writer.java.BeanValidationFactory
import spock.lang.Specification
import spock.lang.Unroll

import static io.openapiprocessor.core.writer.java.AnnotationWriterKt.buildAnnotation
import static io.openapiprocessor.core.writer.java.BeanValidation.DECIMAL_MAX
import static io.openapiprocessor.core.writer.java.BeanValidation.DECIMAL_MIN
import static io.openapiprocessor.core.writer.java.BeanValidation.NOT_NULL
import static io.openapiprocessor.core.writer.java.BeanValidation.SIZE
import static io.openapiprocessor.core.writer.java.BeanValidation.VALID

class BeanValidationFactorySpec extends Specification {
    BeanValidationFactory validation = new BeanValidationFactory ()

    void "applies @Valid to Object" () {
        def dataType = new ObjectDataType(
            'Foo', '', [:], null, false, null)

        when:
        def info = validation.validate (dataType, false)

        then:
        def prop = info.prop
        def io = info.inout

        prop.dataTypeValue == "Foo"
        prop.imports == [VALID.typeName] as Set<String>
        prop.annotations == ["@Valid"]

        io.dataTypeValue == "@Valid Foo"
        io.imports == [VALID.typeName] as Set<String>
        io.annotations == []
    }

    void "does not apply @Valid to non Object types" () {
        def dataType = new OtherDataType()

        when:
        def info = validation.validate (dataType, false)

        then:
        info.annotations == []
    }

    @Unroll
    void "applies @Size to String (minLength: #minLength, maxLength: #maxLength)" () {
        def constraints = new DataTypeConstraints (
            minLength: minLength,
            maxLength: maxLength
        )

        def dataType = new StringDataType(constraints, false, null)

        when:
        def info = validation.validate (dataType, false)

        then:
        info.annotations.collect {it.import }.containsAll (resultImports)
        info.annotations.collect { buildAnnotation (it)}.containsAll (resultAnnotations)

        where:
        // minLength defaults to 0 if not set
        minLength | maxLength || resultImports   | resultAnnotations
        0         | null      || []              | []
        1         | null      || [SIZE.typeName] | ["@Size(min = 1)"]
        1         | 2         || [SIZE.typeName] | ["@Size(min = 1, max = 2)"]
        0         | null      || []              | []
        0         | 0         || [SIZE.typeName] | ["@Size(max = 0)"]
        0         | 2         || [SIZE.typeName] | ["@Size(max = 2)"]
    }

    @Unroll
    void "applies @Size to Array (minItems: #minItems, maxItems: #maxItems)" () {
        def constraints = new DataTypeConstraints (
            minItems: minItems,
            maxItems: maxItems
        )

        DataType dataType = new ArrayDataType(new NoneDataType(), constraints, false)

        when:
        def info = validation.validate (dataType, false)

        then:
        info.annotations.collect {it.import }.containsAll (resultImports)
        info.annotations.collect {buildAnnotation (it)}.containsAll (resultAnnotations)

        where:
        // minItems defaults to 0 if not set
        minItems | maxItems || resultImports   | resultAnnotations
        0        | null     || []              | []
        1        | null     || [SIZE.typeName] | ["@Size(min = 1)"]
        1        | 2        || [SIZE.typeName] | ["@Size(min = 1, max = 2)"]
        0        | null     || []              | []
        0        | 0        || [SIZE.typeName] | ["@Size(max = 0)"]
        0        | 2        || [SIZE.typeName] | ["@Size(max = 2)"]
    }

    @Unroll
    void "applies @Size to Collection (minItems: #minItems, maxItems: #maxItems)" () {
        def constraints = new DataTypeConstraints (
            minItems: minItems,
            maxItems: maxItems
        )

        DataType dataType = new MappedCollectionDataType(
            Collection.name,
            Collection.packageName,
            new StringDataType (),
            constraints,
            false)

        when:
        def info = validation.validate (dataType, false)

        then:
        info.annotations.collect {it.import }.containsAll (resultImports)
        info.annotations.collect {buildAnnotation (it)}.containsAll (resultAnnotations)

        where:
        // minItems defaults to 0 if not set
        minItems | maxItems || resultImports | resultAnnotations
        0        | null     || []            | []
        1        | null     || [SIZE.typeName] | ["@Size(min = 1)"]
        1        | 2        || [SIZE.typeName] | ["@Size(min = 1, max = 2)"]
        0        | null     || []            | []
        0        | 0        || [SIZE.typeName] | ["@Size(max = 0)"]
        0        | 2        || [SIZE.typeName] | ["@Size(max = 2)"]
    }

    @Unroll
    void "applies @NotNull (required: #required, type: #type)" () {
        DataType dataType = createDataType (type, new DataTypeConstraints ())

        when:
        def info = validation.validate (dataType, required)

        then:
        info.annotations.collect {it.import }.containsAll (resultImports)
        info.annotations.collect {buildAnnotation (it)}.containsAll (resultAnnotations)

        where:
        type                     | required || resultImports       | resultAnnotations
        IntegerDataType          | false    || []                  | []
        IntegerDataType          | true     || [NOT_NULL.typeName] | ["@NotNull"]
        StringDataType           | false    || []                  | []
        StringDataType           | true     || [NOT_NULL.typeName] | ["@NotNull"]
        MappedCollectionDataType | false    || []                  | []
        MappedCollectionDataType | true     || [NOT_NULL.typeName] | ["@NotNull"]
    }

    @Unroll
    void "applies @DecimalMin (minimum: #minimum, exclusiveMinimum: #exclusiveMinimum, type: #type)" () {
        def constraints = new DataTypeConstraints (
            minimum: minimum,
            exclusiveMinimum: exclusiveMinimum
        )

        DataType dataType = createDataType (type, constraints)

        when:
        def info = validation.validate (dataType, false)

        then:
        info.annotations.collect {it.import }.containsAll (resultImports)
        info.annotations.collect {buildAnnotation (it)}.containsAll (resultAnnotations)

        where:
        // exclusiveMinimum defaults to false if not set
        type            | minimum | exclusiveMinimum || resultImports          | resultAnnotations
        IntegerDataType | null    | true             || []                     | []
        IntegerDataType | null    | false            || []                     | []
        IntegerDataType | 1       | true             || [DECIMAL_MIN.typeName] | ['@DecimalMin(value = "1", inclusive = false)']
        IntegerDataType | 1       | false            || [DECIMAL_MIN.typeName] | ['@DecimalMin(value = "1")']
        IntegerDataType | 0       | false            || [DECIMAL_MIN.typeName] | ['@DecimalMin(value = "0")']
        LongDataType    | null    | true             || []                     | []
        LongDataType    | null    | false            || []                     | []
        LongDataType    | 1       | true             || [DECIMAL_MIN.typeName] | ['@DecimalMin(value = "1", inclusive = false)']
        LongDataType    | 1       | false            || [DECIMAL_MIN.typeName] | ['@DecimalMin(value = "1")']
        FloatDataType   | null    | true             || []                     | []
        FloatDataType   | null    | false            || []                     | []
        FloatDataType   | 1       | true             || [DECIMAL_MIN.typeName] | ['@DecimalMin(value = "1", inclusive = false)']
        FloatDataType   | 1       | false            || [DECIMAL_MIN.typeName] | ['@DecimalMin(value = "1")']
        DoubleDataType  | null    | true             || []                     | []
        DoubleDataType  | null    | false            || []                     | []
        DoubleDataType  | 1       | true             || [DECIMAL_MIN.typeName] | ['@DecimalMin(value = "1", inclusive = false)']
        DoubleDataType  | 1       | false            || [DECIMAL_MIN.typeName] | ['@DecimalMin(value = "1")']
    }

    @Unroll
    void "applies @DecimalMax (maximum: #maximum, exclusiveMaximum: #exclusiveMaximum, type: #type)" () {
        def constraints = new DataTypeConstraints (
            maximum: maximum,
            exclusiveMaximum: exclusiveMaximum
        )

        DataType dataType = createDataType (type, constraints)

        when:
        def info = validation.validate (dataType, false)

        then:
        info.annotations.collect {it.import }.containsAll (resultImports)
        info.annotations.collect {buildAnnotation (it)}.containsAll (resultAnnotations)

        where:
        // exclusiveMaximum defaults to false if not set
        type            | maximum | exclusiveMaximum || resultImports          | resultAnnotations
        IntegerDataType | null    | true             || []                     | []
        IntegerDataType | null    | false            || []                     | []
        IntegerDataType | 1       | true             || [DECIMAL_MAX.typeName] | ['@DecimalMax(value = "1", inclusive = false)']
        IntegerDataType | 1       | false            || [DECIMAL_MAX.typeName] | ['@DecimalMax(value = "1")']
        IntegerDataType | 0       | false            || [DECIMAL_MAX.typeName] | ['@DecimalMax(value = "0")']
        LongDataType    | null    | true             || []                     | []
        LongDataType    | null    | false            || []                     | []
        LongDataType    | 1       | true             || [DECIMAL_MAX.typeName] | ['@DecimalMax(value = "1", inclusive = false)']
        LongDataType    | 1       | false            || [DECIMAL_MAX.typeName] | ['@DecimalMax(value = "1")']
        FloatDataType   | null    | true             || []                     | []
        FloatDataType   | null    | false            || []                     | []
        FloatDataType   | 1       | true             || [DECIMAL_MAX.typeName] | ['@DecimalMax(value = "1", inclusive = false)']
        FloatDataType   | 1       | false            || [DECIMAL_MAX.typeName] | ['@DecimalMax(value = "1")']
        DoubleDataType  | null    | true             || []                     | []
        DoubleDataType  | null    | false            || []                     | []
        DoubleDataType  | 1       | true             || [DECIMAL_MAX.typeName] | ['@DecimalMax(value = "1", inclusive = false)']
        DoubleDataType  | 1       | false            || [DECIMAL_MAX.typeName] | ['@DecimalMax(value = "1")']
    }

    @Unroll
    void "applies @DecimalMin & @DecimalMax (minimum: #minimum, exclusiveMinimum: #exclusiveMinimum maximum: #maximum, exclusiveMaximum: #exclusiveMaximum)" () {
        def constraints = new DataTypeConstraints ()
        constraints.minimum = minimum
        constraints.exclusiveMinimum = exclusiveMinimum
        constraints.maximum = maximum
        constraints.exclusiveMaximum = exclusiveMaximum

        DataType dataType = new DoubleDataType (constraints, false, null)

        when:
        def info = validation.validate (dataType, false)

        then:
        info.annotations.collect {it.import }.containsAll (resultImports)
        info.annotations.collect {buildAnnotation (it)}.containsAll (resultAnnotations)

        where:
        minimum | exclusiveMinimum | maximum | exclusiveMaximum || resultImports                                | resultAnnotations
        1       | false            | 2       | false            || [DECIMAL_MIN.typeName, DECIMAL_MAX.typeName] | ['@DecimalMin(value = "1")', '@DecimalMax(value = "2")']
        1       | true             | 2       | false            || [DECIMAL_MIN.typeName, DECIMAL_MAX.typeName] | ['@DecimalMin(value = "1", inclusive = false)', '@DecimalMax(value = "2")']
        1       | false            | 2       | true             || [DECIMAL_MIN.typeName, DECIMAL_MAX.typeName] | ['@DecimalMin(value = "1")', '@DecimalMax(value = "2", inclusive = false)']
        1       | true             | 2       | true             || [DECIMAL_MIN.typeName, DECIMAL_MAX.typeName] | ['@DecimalMin(value = "1", inclusive = false)', '@DecimalMax(value = "2", inclusive = false)']
        1       | true             | null    | true             || [DECIMAL_MIN.typeName]                       | ['@DecimalMin(value = "1", inclusive = false)']
        null    | true             | 2       | true             || [DECIMAL_MAX.typeName]                       | ['@DecimalMax(value = "2", inclusive = false)']
    }

    private DataType createDataType (Class clazz, DataTypeConstraints constraints) {
        switch (clazz) {
            case IntegerDataType:
                return new IntegerDataType(constraints, false, null)

            case LongDataType:
                return new LongDataType(constraints, false, null)

            case FloatDataType:
                return new FloatDataType(constraints, false, null)

            case DoubleDataType:
                return new DoubleDataType(constraints, false, null)

            case StringDataType:
                return new StringDataType(constraints, false, null)

            case MappedCollectionDataType:
                return new MappedCollectionDataType(
                    Collection.name,
                    Collection.packageName,
                    new StringDataType (),
                    constraints,
                    false
                )
        }
        null
    }

    class OtherDataType implements DataType {

        @Override
        String getName () {
            'other'
        }

        @Override
        String getTypeName () {
            name
        }

        @Override
        String getPackageName () {
            'other'
        }

        @Override
        Set<String> getImports () {
            []
        }

        @Override
        Set<String> getReferencedImports () {
            []
        }

        @Override
        DataTypeConstraints getConstraints () {
            return null
        }

        @Override
        boolean getDeprecated () {
            return false
        }

        @Override
        Documentation getDocumentation () {
            return null
        }

    }

}
