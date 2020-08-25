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

import com.github.hauner.openapi.core.model.datatypes.DataTypeBase
import io.openapiprocessor.core.model.datatypes.MappedCollectionDataType
import com.github.hauner.openapi.core.model.datatypes.NoneDataType
import io.openapiprocessor.core.model.datatypes.ArrayDataType
import io.openapiprocessor.core.model.datatypes.DataType
import io.openapiprocessor.core.model.datatypes.DataTypeConstraints
import io.openapiprocessor.core.model.datatypes.DoubleDataType
import io.openapiprocessor.core.model.datatypes.FloatDataType
import io.openapiprocessor.core.model.datatypes.IntegerDataType
import io.openapiprocessor.core.model.datatypes.LongDataType
import com.github.hauner.openapi.core.model.datatypes.ObjectDataType
import com.github.hauner.openapi.core.model.datatypes.StringDataType
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
        def dataType = new ObjectDataType()

        when:
        def imports = validation.collectImports (dataType)
        def annotations = validation.createAnnotations (dataType)

        then:
        imports == resultImports as Set<String>
        annotations == resultAnnnotation

        where:
        resultImports     | resultAnnnotation
        [VALID, NOT_NULL] | "@Valid @NotNull"
    }

    void "does not apply @Valid to non Object types" () {
        def dataType = new OtherDataType()

        when:
        def imports = validation.collectImports (dataType)
        def annotations = validation.createAnnotations (dataType)

        then:
        imports == resultImports as Set<String>
        annotations == resultAnnnotation

        where:
        resultImports | resultAnnnotation
        [NOT_NULL]    | "@NotNull"
    }

    @Unroll
    void "applies @Size to String (minLength: #minLength, maxLength: #maxLength)" () {
        def constraints = new DataTypeConstraints ()
        constraints.minLength = minLength
        constraints.maxLength = maxLength

        def dataType = new StringDataType(constraints: constraints)

        when:
        def imports = validation.collectImports (dataType)
        def annotations = validation.createAnnotations (dataType)

        then:
        containsImports (imports, resultImports)
        containsAnnotations (annotations, resultAnnotations)

        where:
        minLength | maxLength || resultImports | resultAnnotations
        null      | null      || []            | ""
        0         | null      || []            | ""
        1         | null      || [SIZE]        | "@Size(min = 1)"
        null      | 0         || [SIZE]        | "@Size(max = 0)"
        null      | 2         || [SIZE]        | "@Size(max = 2)"
        1         | 2         || [SIZE]        | "@Size(min = 1, max = 2)"
    }

    @Unroll
    void "applies @Size to Array (minItems: #minItems, maxItems: #maxItems)" () {
        def constraints = new DataTypeConstraints ()
        constraints.minItems = minItems
        constraints.maxItems = maxItems

        DataType dataType = new ArrayDataType(new NoneDataType(), constraints, false)

        when:
        def imports = validation.collectImports (dataType)
        def annotations = validation.createAnnotations (dataType)

        then:
        imports.containsAll (resultImports)
        annotations.contains (resultAnnotations)

        where:
        minItems | maxItems || resultImports | resultAnnotations
        null     | null     || []            | ""
        0        | null     || []            | ""
        1        | null     || [SIZE]        | "@Size(min = 1)"
        null     | 0        || [SIZE]        | "@Size(max = 0)"
        null     | 2        || [SIZE]        | "@Size(max = 2)"
        1        | 2        || [SIZE]        | "@Size(min = 1, max = 2)"
    }

    @Unroll
    void "applies @Size to Collection (minItems: #minItems, maxItems: #maxItems)" () {
        def constraints = new DataTypeConstraints ()
        constraints.minItems = minItems
        constraints.maxItems = maxItems

        DataType dataType = new MappedCollectionDataType(
            Collection.name,
            Collection.packageName,
            new StringDataType (),
            constraints,
            false)

        when:
        def imports = validation.collectImports (dataType)
        def annotations = validation.createAnnotations (dataType)

        then:
        imports.containsAll (resultImports)
        annotations.contains (resultAnnotations)

        where:
        minItems | maxItems || resultImports    | resultAnnotations
        null     | null     || []       | ""
        0        | null     || []       | ""
        1        | null     || [SIZE] | "@Size(min = 1)"
        null     | 0        || [SIZE] | "@Size(max = 0)"
        null     | 2        || [SIZE] | "@Size(max = 2)"
        1        | 2        || [SIZE] | "@Size(min = 1, max = 2)"
    }

    @Unroll
    void "applies @NotNull (nullable: #nullable, type: #type)" () {
        def constraints = new DataTypeConstraints ()
        constraints.nullable = nullable

        DataType dataType = createDataType (type, constraints)

        when:
        def imports = validation.collectImports (dataType)
        def annotations = validation.createAnnotations (dataType)

        then:
        imports.containsAll (resultImports)
        annotations.contains (resultAnnotations)

        where:
        type                     | nullable || resultImports | resultAnnotations
        IntegerDataType          | null     || []            | ""
        IntegerDataType          | true     || []            | ""
        IntegerDataType          | false    || [NOT_NULL]    | "@NotNull"
        StringDataType           | null     || []            | ""
        StringDataType           | true     || []            | ""
        StringDataType           | false    || [NOT_NULL]    | "@NotNull"
        MappedCollectionDataType | null     || []            | ""
        MappedCollectionDataType | true     || []            | ""
        MappedCollectionDataType | false    || [NOT_NULL]    | "@NotNull"
    }

    @Unroll
    void "applies @DecimalMin (minimum: #minimum, exclusiveMinimum: #exclusiveMinimum, type: #type)" () {
        def constraints = new DataTypeConstraints ()
        constraints.minimum = minimum
        constraints.exclusiveMinimum = exclusiveMinimum

        DataType dataType = createDataType (type, constraints)

        when:
        def imports = validation.collectImports (dataType)
        def annotations = validation.createAnnotations (dataType)

        then:
        imports.containsAll (resultImports)
        annotations.contains (resultAnnotations)

        where:
        type            | minimum | exclusiveMinimum || resultImports | resultAnnotations
        IntegerDataType | null    | null             || []            | ""
        IntegerDataType | null    | true             || []            | ""
        IntegerDataType | null    | false            || []            | ""
        IntegerDataType | 1       | null             || [DECIMAL_MIN] | '@DecimalMin(value = "1")'
        IntegerDataType | 1       | true             || [DECIMAL_MIN] | '@DecimalMin(value = "1", inclusive = false)'
        IntegerDataType | 1       | false            || [DECIMAL_MIN] | '@DecimalMin(value = "1")'
        IntegerDataType | 0       | false            || [DECIMAL_MIN] | '@DecimalMin(value = "0")'
        LongDataType    | null    | null             || []            | ""
        LongDataType    | null    | true             || []            | ""
        LongDataType    | null    | false            || []            | ""
        LongDataType    | 1       | null             || [DECIMAL_MIN] | '@DecimalMin(value = "1")'
        LongDataType    | 1       | true             || [DECIMAL_MIN] | '@DecimalMin(value = "1", inclusive = false)'
        LongDataType    | 1       | false            || [DECIMAL_MIN] | '@DecimalMin(value = "1")'
        FloatDataType   | null    | null             || []            | ""
        FloatDataType   | null    | true             || []            | ""
        FloatDataType   | null    | false            || []            | ""
        FloatDataType   | 1       | null             || [DECIMAL_MIN] | '@DecimalMin(value = "1")'
        FloatDataType   | 1       | true             || [DECIMAL_MIN] | '@DecimalMin(value = "1", inclusive = false)'
        FloatDataType   | 1       | false            || [DECIMAL_MIN] | '@DecimalMin(value = "1")'
        DoubleDataType  | null    | null             || []            | ""
        DoubleDataType  | null    | true             || []            | ""
        DoubleDataType  | null    | false            || []            | ""
        DoubleDataType  | 1       | null             || [DECIMAL_MIN] | '@DecimalMin(value = "1")'
        DoubleDataType  | 1       | true             || [DECIMAL_MIN] | '@DecimalMin(value = "1", inclusive = false)'
        DoubleDataType  | 1       | false            || [DECIMAL_MIN] | '@DecimalMin(value = "1")'
        StringDataType  | 1       | null             || []            | ""
    }

    @Unroll
    void "applies @DecimalMax (maximum: #maximum, exclusiveMaximum: #exclusiveMaximum, type: #type)" () {
        def constraints = new DataTypeConstraints ()
        constraints.maximum = maximum
        constraints.exclusiveMaximum = exclusiveMaximum

        DataType dataType = createDataType (type, constraints)

        when:
        def imports = validation.collectImports (dataType)
        def annotations = validation.createAnnotations (dataType)

        then:
        imports.containsAll (resultImports)
        annotations.contains (resultAnnotations)

        where:
        type            | maximum | exclusiveMaximum || resultImports | resultAnnotations
        IntegerDataType | null    | null             || []            | ""
        IntegerDataType | null    | true             || []            | ""
        IntegerDataType | null    | false            || []            | ""
        IntegerDataType | 1       | null             || [DECIMAL_MAX] | '@DecimalMax(value = "1")'
        IntegerDataType | 1       | true             || [DECIMAL_MAX] | '@DecimalMax(value = "1", inclusive = false)'
        IntegerDataType | 1       | false            || [DECIMAL_MAX] | '@DecimalMax(value = "1")'
        IntegerDataType | 0       | false            || [DECIMAL_MAX] | '@DecimalMax(value = "0")'
        LongDataType    | null    | null             || []            | ""
        LongDataType    | null    | true             || []            | ""
        LongDataType    | null    | false            || []            | ""
        LongDataType    | 1       | null             || [DECIMAL_MAX] | '@DecimalMax(value = "1")'
        LongDataType    | 1       | true             || [DECIMAL_MAX] | '@DecimalMax(value = "1", inclusive = false)'
        LongDataType    | 1       | false            || [DECIMAL_MAX] | '@DecimalMax(value = "1")'
        FloatDataType   | null    | null             || []            | ""
        FloatDataType   | null    | true             || []            | ""
        FloatDataType   | null    | false            || []            | ""
        FloatDataType   | 1       | null             || [DECIMAL_MAX] | '@DecimalMax(value = "1")'
        FloatDataType   | 1       | true             || [DECIMAL_MAX] | '@DecimalMax(value = "1", inclusive = false)'
        FloatDataType   | 1       | false            || [DECIMAL_MAX] | '@DecimalMax(value = "1")'
        DoubleDataType  | null    | null             || []            | ""
        DoubleDataType  | null    | true             || []            | ""
        DoubleDataType  | null    | false            || []            | ""
        DoubleDataType  | 1       | null             || [DECIMAL_MAX] | '@DecimalMax(value = "1")'
        DoubleDataType  | 1       | true             || [DECIMAL_MAX] | '@DecimalMax(value = "1", inclusive = false)'
        DoubleDataType  | 1       | false            || [DECIMAL_MAX] | '@DecimalMax(value = "1")'
        StringDataType  | 1       | null             || []            | ""
    }

    @Unroll
    void "applies @DecimalMin & @DecimalMax (minimum: #minimum, exclusiveMinimum: #exclusiveMinimum maximum: #maximum, exclusiveMaximum: #exclusiveMaximum)" () {
        def constraints = new DataTypeConstraints ()
        constraints.minimum = minimum
        constraints.exclusiveMinimum = exclusiveMinimum
        constraints.maximum = maximum
        constraints.exclusiveMaximum = exclusiveMaximum

        DataType dataType = new DoubleDataType (constraints, false)

        when:
        def imports = validation.collectImports (dataType)
        def annotations = validation.createAnnotations (dataType)

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
                return new IntegerDataType(constraints, false)

            case LongDataType:
                return new LongDataType(constraints, false)

            case FloatDataType:
                return new FloatDataType(constraints, false)

            case DoubleDataType:
                return new DoubleDataType(constraints, false)

            case StringDataType:
                return new StringDataType(constraints: constraints)

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
