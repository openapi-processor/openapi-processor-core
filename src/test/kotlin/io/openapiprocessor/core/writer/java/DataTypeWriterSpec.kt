/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.string.shouldContain
import io.openapiprocessor.core.converter.ApiOptions
import io.openapiprocessor.core.converter.mapping.Annotation as MappingAnnotation
import io.openapiprocessor.core.converter.mapping.AnnotationTypeMapping
import io.openapiprocessor.core.extractImports
import io.openapiprocessor.core.model.datatypes.*
import io.openapiprocessor.core.support.datatypes.ObjectDataType
import io.openapiprocessor.core.support.datatypes.ListDataType
import io.openapiprocessor.core.support.datatypes.propertyDataType
import io.openapiprocessor.core.support.datatypes.propertyDataTypeString
import java.io.StringWriter

class DataTypeWriterSpec: StringSpec({

    val options = ApiOptions()
    val generatedWriter = SimpleGeneratedWriter(options)
    var writer = DataTypeWriter(options, generatedWriter, BeanValidationFactory())
    val target = StringWriter()

    "writes @Generated annotation import" {
        val dataType = ObjectDataType("Foo", "pkg", linkedMapOf(
            Pair("foo", propertyDataTypeString())
        ))

        // when:
        writer.write(target, dataType)

        // then:
        val imports = extractImports(target)
        imports shouldContain "import io.openapiprocessor.generated.support.Generated;"
    }

    "writes @Generated annotation" {
        val dataType = ObjectDataType("Foo", "pkg", linkedMapOf(
            Pair("foo", propertyDataTypeString())
        ))

        // when:
        writer.write(target, dataType)

        // then:
        target.toString() shouldContain
            """    
            |@Generated
            |public class Foo {
            |
            """.trimMargin()
    }

    "writes @NotNull import for required property" {
        options.beanValidation = true

        val dataType = ObjectDataType("Foo", "pkg", linkedMapOf(
            Pair("foo", propertyDataTypeString())
        ), DataTypeConstraints(required = listOf("foo")), false)

        // when:
        writer.write(target, dataType)

        // then:
        val imports = extractImports(target)
        imports shouldContain "import javax.validation.constraints.NotNull;"
    }

    "writes required property with @NotNull annotation" {
        options.beanValidation = true

        val dataType = ObjectDataType("Foo", "pkg", linkedMapOf(
            Pair("foo", propertyDataTypeString())
        ), DataTypeConstraints(required = listOf("foo")), false)

        // when:
        writer.write(target, dataType)

        // then:
        target.toString() shouldContain
            """    
            |    @NotNull
            |    @JsonProperty("foo")
            |    private String foo;
            |
            """.trimMargin()
    }

    "writes import of nested generic list type" {
        val dataType = ObjectDataType("Foo", "pkg",
            linkedMapOf("foos" to propertyDataType(ListDataType(StringDataType()))
        ))

        // when:
        writer.write(target, dataType)

        // then:
        val imports = extractImports(target)
        imports shouldContain "import java.util.List;"
    }

    "writes additional bean validation object annotation import" {
        options.beanValidation = true
        val validation = object : BeanValidationFactory() {
            override fun validate(dataType: ModelDataType): BeanValidationInfo {
                return BeanValidationInfoSimple(dataType, listOf(Annotation("foo.Foo")))
            }
        }

        writer = DataTypeWriter(options, generatedWriter, validation)

        val dataType = ObjectDataType("Foo",
            "pkg", linkedMapOf("foo" to propertyDataTypeString()))

        // when:
        writer.write(target, dataType)

        // then:
        val imports = extractImports(target)
        imports shouldContain "import foo.Foo;"
    }

    "writes additional bean validation object annotation" {
        options.beanValidation = true
        val validation = object : BeanValidationFactory() {
            override fun validate(dataType: ModelDataType): BeanValidationInfo {
                return BeanValidationInfoSimple(dataType, listOf(Annotation("foo.Foo")))
            }
        }
        writer = DataTypeWriter(options, generatedWriter, validation)

        val dataType = ObjectDataType("Foo",
            "pkg", linkedMapOf("foo" to propertyDataTypeString()))

        // when:
        writer.write(target, dataType)

        // then:
        target.toString() shouldContain
            """    
            |@Foo
            |@Generated
            |public class Foo {
            |
            """.trimMargin()
    }

    "writes properties with @JsonProperty access annotation" {
        val dataType = ObjectDataType ("Foo", "pkg", linkedMapOf(
            "foo" to PropertyDataType (true, false, StringDataType ()),
            "bar" to PropertyDataType (false, true, StringDataType ())
        ))

        // when:
        writer.write (target, dataType)

        // then:
        target.toString () shouldContain
            """
            |    @JsonProperty(value = "foo", access = JsonProperty.Access.READ_ONLY)
            |    private String foo;
            |
            |    @JsonProperty(value = "bar", access = JsonProperty.Access.WRITE_ONLY)
            |    private String bar;
            """.trimMargin()
    }

    "writes class with implements" {
        options.oneOfInterface = true

        val dataType = ObjectDataType ("Foo", "pkg", linkedMapOf(
            "foo" to PropertyDataType (false, false, StringDataType ())
        ))

        val ifDataType = InterfaceDataType(
            DataTypeName("MarkerInterface"), "pkg", listOf(dataType))

        dataType.implementsDataType = ifDataType

        // when:
        writer.write (target, dataType)

        target.toString() shouldContain ("public class Foo implements MarkerInterface {")
    }

    "writes additional object annotation import from annotation mapping" {
        options.typeMappings = listOf(
            AnnotationTypeMapping(
                "Foo", annotation = MappingAnnotation("foo.Bar")
            ))
        writer = DataTypeWriter(options, headerWriter, BeanValidationFactory())

        val dataType = ObjectDataType("Foo",
            "pkg", linkedMapOf("foo" to propertyDataTypeString()))

        // when:
        writer.write(target, dataType)

        // then:
        val imports = extractImports(target)
        imports shouldContain "import foo.Bar;"
    }

    "writes additional object annotation from annotation mapping" {
        options.typeMappings = listOf(
            AnnotationTypeMapping(
                "Foo", annotation = MappingAnnotation(
                    "foo.Bar", parametersX = linkedMapOf("bar" to """"rab"""")
                )
            ))
        writer = DataTypeWriter(options, headerWriter, BeanValidationFactory())

        val dataType = ObjectDataType("Foo",
            "pkg", linkedMapOf("foo" to propertyDataTypeString()))

        // when:
        writer.write(target, dataType)

        // then:
        target.toString() shouldContain
            """    
            |@Bar(bar = "rab")
            |public class Foo {
            |
            """.trimMargin()
    }
})
