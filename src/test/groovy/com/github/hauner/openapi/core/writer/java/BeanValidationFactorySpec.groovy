/*
 * Copyright 2020 the original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.hauner.openapi.core.writer.java

import io.openapiprocessor.core.model.datatypes.DataTypeBase
import io.openapiprocessor.core.model.datatypes.MappedCollectionDataType
import io.openapiprocessor.core.model.datatypes.NoneDataType
import io.openapiprocessor.core.model.datatypes.ArrayDataType
import io.openapiprocessor.core.model.datatypes.DataType
import io.openapiprocessor.core.model.datatypes.DataTypeConstraints
import io.openapiprocessor.core.model.datatypes.DoubleDataType
import io.openapiprocessor.core.model.datatypes.FloatDataType
import io.openapiprocessor.core.model.datatypes.IntegerDataType
import io.openapiprocessor.core.model.datatypes.LongDataType
import io.openapiprocessor.core.model.datatypes.ObjectDataType
import io.openapiprocessor.core.model.datatypes.StringDataType
import io.openapiprocessor.core.writer.java.BeanValidationFactory
import spock.lang.Specification
import spock.lang.Unroll

class BeanValidationFactorySpec extends Specification {

    public static final String NOT_NULL = "javax.validation.constraints.NotNull"
    public static final String SIZE = "javax.validation.constraints.Size"
    public static final String VALID = "javax.validation.Valid"
    public static final String DECIMAL_MIN = "javax.validation.constraints.DecimalMin"
    public static final String DECIMAL_MAX = "javax.validation.constraints.DecimalMax"

    BeanValidationFactory validation = new BeanValidationFactory ()

    @Unroll
    void "applies @Valid to Object" () {
        def dataType = new ObjectDataType(
            'Foo', '', [:], null, false, null)

        when:
        def imports = validation.collectImports (dataType, false)
        def annotations = validation.createAnnotations (dataType, false)

        then:
        imports == resultImports as Set<String>
        annotations == resultAnnnotation

        where:
        resultImports     | resultAnnnotation
        [VALID]           | "@Valid"
    }

    void "does not apply @Valid to non Object types" () {
        def dataType = new OtherDataType()

        when:
        def imports = validation.collectImports (dataType, false)
        def annotations = validation.createAnnotations (dataType, false)

        then:
        imports == resultImports as Set<String>
        annotations == resultAnnnotation

        where:
        resultImports | resultAnnnotation
        []            | ""
    }

    @Unroll
    void "applies @Size to String (minLength: #minLength, maxLength: #maxLength)" () {
        def constraints = new DataTypeConstraints (
            minLength: minLength,
            maxLength: maxLength
        )

        def dataType = new StringDataType(constraints, false, null)

        when:
        def imports = validation.collectImports (dataType, false)
        def annotations = validation.createAnnotations (dataType, false)

        then:
        containsImports (imports, resultImports)
        containsAnnotations (annotations, resultAnnotations)

        where:
        minLength | maxLength || resultImports | resultAnnotations
        0         | null      || []            | ""
        1         | null      || [SIZE]        | "@Size(min = 1)"
        1         | 2         || [SIZE]        | "@Size(min = 1, max = 2)"
//      minLength defaults to 0 if not set
//      null      | null      || []            | ""
//      null      | 0         || [SIZE]        | "@Size(max = 0)"
//      null      | 2         || [SIZE]        | "@Size(max = 2)"
        0         | null      || []            | ""
        0         | 0         || [SIZE]        | "@Size(max = 0)"
        0         | 2         || [SIZE]        | "@Size(max = 2)"
    }

    @Unroll
    void "applies @Size to Array (minItems: #minItems, maxItems: #maxItems)" () {
        def constraints = new DataTypeConstraints (
            minItems: minItems,
            maxItems: maxItems
        )

        DataType dataType = new ArrayDataType(new NoneDataType(), constraints, false)

        when:
        def imports = validation.collectImports (dataType, false)
        def annotations = validation.createAnnotations (dataType, false)

        then:
        imports.containsAll (resultImports)
        annotations.contains (resultAnnotations)

        where:
        minItems | maxItems || resultImports | resultAnnotations
        0        | null     || []            | ""
        1        | null     || [SIZE]        | "@Size(min = 1)"
        1        | 2        || [SIZE]        | "@Size(min = 1, max = 2)"
//      minItems defaults to 0 if not set
//      null     | null     || []            | ""
//      null     | 0        || [SIZE]        | "@Size(max = 0)"
//      null     | 2        || [SIZE]        | "@Size(max = 2)"
        0        | null     || []            | ""
        0        | 0        || [SIZE]        | "@Size(max = 0)"
        0        | 2        || [SIZE]        | "@Size(max = 2)"
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
        def imports = validation.collectImports (dataType, false)
        def annotations = validation.createAnnotations (dataType, false)

        then:
        imports.containsAll (resultImports)
        annotations.contains (resultAnnotations)

        where:
        minItems | maxItems || resultImports    | resultAnnotations
        0        | null     || []       | ""
        1        | null     || [SIZE] | "@Size(min = 1)"
        1        | 2        || [SIZE] | "@Size(min = 1, max = 2)"
//      minItems defaults to 0 if not set
//      null     | null     || []            | ""
//      null     | 0        || [SIZE]        | "@Size(max = 0)"
//      null     | 2        || [SIZE]        | "@Size(max = 2)"
        0        | null     || []            | ""
        0        | 0        || [SIZE]        | "@Size(max = 0)"
        0        | 2        || [SIZE]        | "@Size(max = 2)"
    }

    @Unroll
    void "applies @NotNull (required: #required, type: #type)" () {
        DataType dataType = createDataType (type, new DataTypeConstraints ())

        when:
        def imports = validation.collectImports (dataType, required)
        def annotations = validation.createAnnotations (dataType, required)

        then:
        imports.containsAll (resultImports)
        annotations.contains (resultAnnotations)

        where:
        type                     | required || resultImports | resultAnnotations
        IntegerDataType          | false    || []            | ""
        IntegerDataType          | true     || [NOT_NULL]    | "@NotNull"
        StringDataType           | false    || []            | ""
        StringDataType           | true     || [NOT_NULL]    | "@NotNull"
        MappedCollectionDataType | false    || []            | ""
        MappedCollectionDataType | true     || [NOT_NULL]    | "@NotNull"
    }

    @Unroll
    void "applies @DecimalMin (minimum: #minimum, exclusiveMinimum: #exclusiveMinimum, type: #type)" () {
        def constraints = new DataTypeConstraints (
            minimum: minimum,
            exclusiveMinimum: exclusiveMinimum
        )

        DataType dataType = createDataType (type, constraints)

        when:
        def imports = validation.collectImports (dataType, false)
        def annotations = validation.createAnnotations (dataType, false)

        then:
        imports.containsAll (resultImports)
        annotations.contains (resultAnnotations)

        where:
        type            | minimum | exclusiveMinimum || resultImports | resultAnnotations
//      exclusiveMinimum defaults to false if not set
//      IntegerDataType | null    | null             || []            | ""
        IntegerDataType | null    | true             || []            | ""
        IntegerDataType | null    | false            || []            | ""
//      IntegerDataType | 1       | null             || [DECIMAL_MIN] | '@DecimalMin(value = "1")'
        IntegerDataType | 1       | true             || [DECIMAL_MIN] | '@DecimalMin(value = "1", inclusive = false)'
        IntegerDataType | 1       | false            || [DECIMAL_MIN] | '@DecimalMin(value = "1")'
        IntegerDataType | 0       | false            || [DECIMAL_MIN] | '@DecimalMin(value = "0")'
//      LongDataType    | null    | null             || []            | ""
        LongDataType    | null    | true             || []            | ""
        LongDataType    | null    | false            || []            | ""
//      LongDataType    | 1       | null             || [DECIMAL_MIN] | '@DecimalMin(value = "1")'
        LongDataType    | 1       | true             || [DECIMAL_MIN] | '@DecimalMin(value = "1", inclusive = false)'
        LongDataType    | 1       | false            || [DECIMAL_MIN] | '@DecimalMin(value = "1")'
//      FloatDataType   | null    | null             || []            | ""
        FloatDataType   | null    | true             || []            | ""
        FloatDataType   | null    | false            || []            | ""
//      FloatDataType   | 1       | null             || [DECIMAL_MIN] | '@DecimalMin(value = "1")'
        FloatDataType   | 1       | true             || [DECIMAL_MIN] | '@DecimalMin(value = "1", inclusive = false)'
        FloatDataType   | 1       | false            || [DECIMAL_MIN] | '@DecimalMin(value = "1")'
//      DoubleDataType  | null    | null             || []            | ""
        DoubleDataType  | null    | true             || []            | ""
        DoubleDataType  | null    | false            || []            | ""
//      DoubleDataType  | 1       | null             || [DECIMAL_MIN] | '@DecimalMin(value = "1")'
        DoubleDataType  | 1       | true             || [DECIMAL_MIN] | '@DecimalMin(value = "1", inclusive = false)'
        DoubleDataType  | 1       | false            || [DECIMAL_MIN] | '@DecimalMin(value = "1")'
//      StringDataType  | 1       | null             || []            | ""
    }

    @Unroll
    void "applies @DecimalMax (maximum: #maximum, exclusiveMaximum: #exclusiveMaximum, type: #type)" () {
        def constraints = new DataTypeConstraints (
            maximum: maximum,
            exclusiveMaximum: exclusiveMaximum
        )

        DataType dataType = createDataType (type, constraints)

        when:
        def imports = validation.collectImports (dataType, false)
        def annotations = validation.createAnnotations (dataType, false)

        then:
        imports.containsAll (resultImports)
        annotations.contains (resultAnnotations)

        where:
        type            | maximum | exclusiveMaximum || resultImports | resultAnnotations
//      exclusiveMaximum defaults to false if not set
//      IntegerDataType | null    | null             || []            | ""
        IntegerDataType | null    | true             || []            | ""
        IntegerDataType | null    | false            || []            | ""
//      IntegerDataType | 1       | null             || [DECIMAL_MAX] | '@DecimalMax(value = "1")'
        IntegerDataType | 1       | true             || [DECIMAL_MAX] | '@DecimalMax(value = "1", inclusive = false)'
        IntegerDataType | 1       | false            || [DECIMAL_MAX] | '@DecimalMax(value = "1")'
        IntegerDataType | 0       | false            || [DECIMAL_MAX] | '@DecimalMax(value = "0")'
//      LongDataType    | null    | null             || []            | ""
        LongDataType    | null    | true             || []            | ""
        LongDataType    | null    | false            || []            | ""
//      LongDataType    | 1       | null             || [DECIMAL_MAX] | '@DecimalMax(value = "1")'
        LongDataType    | 1       | true             || [DECIMAL_MAX] | '@DecimalMax(value = "1", inclusive = false)'
        LongDataType    | 1       | false            || [DECIMAL_MAX] | '@DecimalMax(value = "1")'
//      FloatDataType   | null    | null             || []            | ""
        FloatDataType   | null    | true             || []            | ""
        FloatDataType   | null    | false            || []            | ""
//      FloatDataType   | 1       | null             || [DECIMAL_MAX] | '@DecimalMax(value = "1")'
        FloatDataType   | 1       | true             || [DECIMAL_MAX] | '@DecimalMax(value = "1", inclusive = false)'
        FloatDataType   | 1       | false            || [DECIMAL_MAX] | '@DecimalMax(value = "1")'
//      DoubleDataType  | null    | null             || []            | ""
        DoubleDataType  | null    | true             || []            | ""
        DoubleDataType  | null    | false            || []            | ""
//      DoubleDataType  | 1       | null             || [DECIMAL_MAX] | '@DecimalMax(value = "1")'
        DoubleDataType  | 1       | true             || [DECIMAL_MAX] | '@DecimalMax(value = "1", inclusive = false)'
        DoubleDataType  | 1       | false            || [DECIMAL_MAX] | '@DecimalMax(value = "1")'
//      StringDataType  | 1       | null             || []            | ""
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
        def imports = validation.collectImports (dataType, false)
        def annotations = validation.createAnnotations (dataType, false)

        then:
        imports.containsAll (resultImports)
        annotations.contains (resultAnnotations)

        where:
        minimum | exclusiveMinimum | maximum | exclusiveMaximum || resultImports              | resultAnnotations
        1       | false            | 2       | false            || [DECIMAL_MIN, DECIMAL_MAX] | '@DecimalMin(value = "1") @DecimalMax(value = "2")'
        1       | true             | 2       | false            || [DECIMAL_MIN, DECIMAL_MAX] | '@DecimalMin(value = "1", inclusive = false) @DecimalMax(value = "2")'
        1       | false            | 2       | true             || [DECIMAL_MIN, DECIMAL_MAX] | '@DecimalMin(value = "1") @DecimalMax(value = "2", inclusive = false)'
        1       | true             | 2       | true             || [DECIMAL_MIN, DECIMAL_MAX] | '@DecimalMin(value = "1", inclusive = false) @DecimalMax(value = "2", inclusive = false)'
        1       | true             | null    | true             || [DECIMAL_MIN]              | '@DecimalMin(value = "1", inclusive = false)'
        null    | true             | 2       | true             || [DECIMAL_MAX]              | '@DecimalMax(value = "2", inclusive = false)'
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

    private boolean containsImports (Set source, List match) {
        source.containsAll (match)
    }

    private boolean containsAnnotations (String annotations, String match) {
        if (!match || match.empty) {
            return true
        }

        annotations.contains (match)
    }

    class OtherDataType extends DataTypeBase {

        @Override
        String getName () {
            'other'
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
    }

}
