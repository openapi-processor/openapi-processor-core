/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import io.openapiprocessor.core.model.datatypes.*
import io.openapiprocessor.core.support.datatypes.ListDataType
import io.openapiprocessor.core.support.datatypes.ObjectDataType
import io.openapiprocessor.core.support.datatypes.propertyDataType
import io.openapiprocessor.core.support.datatypes.propertyDataTypeString

class BeanValidationFactorySpec : StringSpec({
    isolationMode = IsolationMode.InstancePerTest

    "applies @Valid to 'array' with object items" {
        val validation = BeanValidationFactory()

        val dataType = ArrayDataType(
            ObjectDataType(
                "Foo", "pkg", linkedMapOf(
                    "foo" to propertyDataTypeString()
                )
            )
        )
        val info = validation.validate(dataType)

        val prop = info.prop
        prop.dataTypeValue shouldBe "Foo[]"
        prop.imports shouldBe setOf(BeanValidation.VALID.typeName)
        prop.annotations shouldBe setOf("@Valid")

        val io = info.inout
        io.dataTypeValue shouldBe "@Valid Foo[]"
        io.imports shouldBe setOf(BeanValidation.VALID.typeName)
        io.annotations.shouldBeEmpty()
    }

    "does not apply @Valid to 'array' with simple items" {
        val validation = BeanValidationFactory()

        val dataType = ArrayDataType(StringDataType())
        val info = validation.validate(dataType)

        val prop = info.prop
        prop.dataTypeValue shouldBe "String[]"
        prop.imports.shouldBeEmpty()
        prop.annotations.shouldBeEmpty()

        val io = info.inout
        io.dataTypeValue shouldBe "String[]"
        io.imports.shouldBeEmpty()
        io.annotations.shouldBeEmpty()
    }

    "applies @Valid to mapped collection with object items" {
        val validation = BeanValidationFactory()

        val dataType = MappedCollectionDataType(
            "List", "pkg",
            ObjectDataType(
                "Foo", "pkg", linkedMapOf("foo" to propertyDataTypeString())
            )
        )
        val info = validation.validate(dataType)

        val prop = info.prop
        prop.dataTypeValue shouldBe "List<@Valid Foo>"
        prop.imports shouldBe setOf(BeanValidation.VALID.typeName)
        prop.annotations.shouldBeEmpty()

        val io = info.inout
        io.dataTypeValue shouldBe "List<@Valid Foo>"
        io.imports shouldBe setOf(BeanValidation.VALID.typeName)
        io.annotations.shouldBeEmpty()
    }

    "does not apply @Valid to mapped collection with simple items" {
        val validation = BeanValidationFactory()

        val dataType = MappedCollectionDataType("List", "pkg", StringDataType())
        val info = validation.validate(dataType)

        val prop = info.prop
        prop.dataTypeValue shouldBe "List<String>"
        prop.imports.shouldBeEmpty()
        prop.annotations.shouldBeEmpty()

        val io = info.inout
        io.dataTypeValue shouldBe "List<String>"
        io.imports.shouldBeEmpty()
        io.annotations.shouldBeEmpty()
    }

    "applies @Pattern to String" {
        val validation = BeanValidationFactory()

        val dataType = StringDataType(DataTypeConstraints(pattern = "regex"))
        val info = validation.validate(dataType)
        info.annotations.size shouldBe 1

        val prop = info.prop
        prop.dataTypeValue shouldBe "String"
        prop.imports shouldBe setOf(BeanValidation.PATTERN.typeName)
        prop.annotations shouldBe listOf("""@Pattern(regexp = "regex")""")

        val io = info.inout
        io.dataTypeValue shouldBe """@Pattern(regexp = "regex") String"""
        io.imports shouldBe setOf(BeanValidation.PATTERN.typeName)
        io.annotations.shouldBeEmpty()
    }

    "applies @Pattern to String with escaping" {
        val validation = BeanValidationFactory()

        val dataType = StringDataType(DataTypeConstraints(pattern = """\.\\"""))
        val info = validation.validate(dataType)

        val prop = info.prop
        prop.dataTypeValue shouldBe "String"
        prop.imports shouldBe setOf(BeanValidation.PATTERN.typeName)
        prop.annotations shouldBe setOf("""@Pattern(regexp = "\\.\\\\")""")

        val io = info.inout
        io.dataTypeValue shouldBe """@Pattern(regexp = "\\.\\\\") String"""
        io.imports shouldBe setOf(BeanValidation.PATTERN.typeName)
        io.annotations.shouldBeEmpty()
    }

    "does apply validation annotations to 'collection' item" {
        val validation = BeanValidationFactory()

        val dataType = ListDataType(
            StringDataType(
                constraints = DataTypeConstraints(minLength = 2, maxLength = 3)
            ),
            constraints = DataTypeConstraints()
        )
        val info = validation.validate(dataType, true)

        val prop = info.prop
        prop.dataTypeValue shouldBe "List<@Size(min = 2, max = 3) String>"
        prop.imports shouldBe setOf(BeanValidation.NOT_NULL.typeName, BeanValidation.SIZE.typeName)
        prop.annotations shouldBe setOf("@NotNull")

        val io = info.inout
        io.dataTypeValue shouldBe "@NotNull List<@Size(min = 2, max = 3) String>"
        io.imports shouldBe setOf(BeanValidation.NOT_NULL.typeName, BeanValidation.SIZE.typeName)
        io.annotations.shouldBeEmpty()
    }

    "does apply validation annotations to 'collection' model items" {
        val validation = BeanValidationFactory()

        val dataType = ListDataType(
            ObjectDataType("Foo", "pkg",
                linkedMapOf("foo" to propertyDataType(StringDataType(
                    constraints = DataTypeConstraints(minLength = 2, maxLength = 3))
                ))))
        val info = validation.validate(dataType, true)

        val prop = info.prop
        prop.dataTypeValue shouldBe "List<@Valid Foo>"
        prop.imports shouldBe setOf(BeanValidation.NOT_NULL.typeName, BeanValidation.VALID.typeName)
        prop.annotations shouldBe setOf("@NotNull")

        val io = info.inout
        io.dataTypeValue shouldBe "@NotNull List<@Valid Foo>"
        io.imports shouldBe setOf(BeanValidation.NOT_NULL.typeName, BeanValidation.VALID.typeName)
        io.annotations.shouldBeEmpty()
    }

    "does apply validation annotations to 'array' item" {
        val validation = BeanValidationFactory()

        val dataType = ArrayDataType(StringDataType(
            constraints = DataTypeConstraints(minLength = 2, maxLength = 3)),
            constraints = DataTypeConstraints())
        val info = validation.validate(dataType, true)

        val prop = info.prop
        prop.dataTypeValue shouldBe "String[]"
        prop.imports shouldBe setOf(BeanValidation.NOT_NULL.typeName)
        prop.annotations shouldBe setOf("@NotNull")

        val io = info.inout
        io.dataTypeValue shouldBe "@NotNull String[]"
        io.imports shouldBe setOf(BeanValidation.NOT_NULL.typeName)
        io.annotations.shouldBeEmpty()
    }

    "does apply validation annotations to 'array' model items" {
        val validation = BeanValidationFactory()

        val dataType = ArrayDataType(
            ObjectDataType("Foo", "pkg",
                linkedMapOf("foo" to propertyDataType(StringDataType(
                    constraints = DataTypeConstraints(minLength = 2, maxLength = 3))
                ))))
        val info = validation.validate(dataType, true)

        val prop = info.prop
        prop.dataTypeValue shouldBe "Foo[]"
        prop.imports shouldBe setOf(BeanValidation.NOT_NULL.typeName, BeanValidation.VALID.typeName)
        prop.annotations shouldBe listOf("@Valid", "@NotNull")

        val io = info.inout
        io.dataTypeValue shouldBe "@Valid @NotNull Foo[]"
        io.imports shouldBe setOf(BeanValidation.NOT_NULL.typeName, BeanValidation.VALID.typeName)
        io.annotations.shouldBeEmpty()
    }

    "applies @Email to String" {
        val validation = BeanValidationFactory()

        val dataType = StringDataType(constraints = DataTypeConstraints(format = "email"))
        val info = validation.validate(dataType)

        val prop = info.prop
        prop.dataTypeValue shouldBe "String"
        prop.imports shouldBe setOf(BeanValidation.EMAIL.typeName)
        prop.annotations shouldBe setOf("@Email")

        val io = info.inout
        io.dataTypeValue shouldBe "@Email String"
        io.imports shouldBe setOf(BeanValidation.EMAIL.typeName)
        io.annotations.shouldBeEmpty()
    }
})
