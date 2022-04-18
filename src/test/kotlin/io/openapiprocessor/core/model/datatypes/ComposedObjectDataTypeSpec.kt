/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.model.datatypes

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.shouldBe
import io.openapiprocessor.core.support.datatypes.ObjectDataType
import io.openapiprocessor.core.support.datatypes.propertyDataType
import io.openapiprocessor.core.support.datatypes.propertyDataTypeString

class ComposedObjectDataTypeSpec : StringSpec({

    "loop properties of allOf objects as if it was a single object" {
        val composed = AllOfObjectDataType(DataTypeName("Foo"), "pkg", listOf(
            ObjectDataType("Foo", "pkg", linkedMapOf(
                    Pair("foo", propertyDataTypeString()),
                    Pair("foobar", propertyDataTypeString()))
            ),
            ObjectDataType("Bar", "pkg", linkedMapOf(
                Pair("bar", propertyDataTypeString()),
                Pair("barfoo", propertyDataTypeString())
            ))
        ))

        composed.properties.keys shouldBe linkedSetOf("foo", "foobar", "bar", "barfoo")
    }

    "allOf object has id name and type name" {
        val composed = AllOfObjectDataType(DataTypeName("Foo", "FooX"), "pkg", listOf())

        composed.getName() shouldBe "Foo"
        composed.getTypeName() shouldBe "FooX"
    }

    "allOf object has creates import with type name" {
        val composed = AllOfObjectDataType(DataTypeName("Foo", "FooX"), "pkg", listOf())

        composed.getImports() shouldBe setOf("pkg.FooX")
    }

    // https://github.com/openapi-processor/openapi-processor-spring/issues/128
    "allOf creates imports for all items" {
        val composed = AllOfObjectDataType(DataTypeName("Foo"), "pkg", listOf(
            ObjectDataType("Foo", "pkg", linkedMapOf(
                "foo" to propertyDataType(OffsetDateTimeDataType())
            )),
            ObjectDataType("Bar", "pkg", linkedMapOf(
                "bar" to propertyDataType(ObjectDataType("BarBar", "pkg", linkedMapOf(
                    "barbar" to propertyDataType(OffsetDateTimeDataType())
                )))
            ))
        ))

        composed.getImports() shouldBe setOf("pkg.Foo")
        composed.referencedImports shouldBe setOf("java.time.OffsetDateTime", "pkg.BarBar")
    }

    "allOf creates does not leak import for type-less item" {
        val composed = AllOfObjectDataType(DataTypeName("Foo"), "pkg", listOf(
            ObjectDataType("Bar", "pkg", linkedMapOf(
                "bar" to propertyDataTypeString()
            )),
            NoDataType(DataTypeName("Leak"), "pkg")
        ))

        composed.getImports() shouldBe setOf("pkg.Foo")
        composed.referencedImports shouldBe setOf("java.lang.String")
    }

    "allOf handles 'required' constraint of all items" {
        val composed = AllOfObjectDataType(DataTypeName("AllOf"), "pkg", listOf(
            ObjectDataType("Foo", "pkg", linkedMapOf(
                "foo" to propertyDataTypeString(),
                "fux" to propertyDataTypeString()
            ), constraints = DataTypeConstraints(required = listOf("foo", "fux"))),
            ObjectDataType(
                "Bar", "pkg", linkedMapOf(
                    "bar" to propertyDataTypeString(),
                    "bux" to propertyDataTypeString()
            ), constraints = DataTypeConstraints(required = listOf("bar", "bux")))
        ))

        composed.constraints!!.required shouldContainAll listOf("foo", "fux", "bar", "bux")
    }

    "allOf without 'required' has null constraints" {
        val composed = AllOfObjectDataType(DataTypeName("AllOf"), "pkg", listOf(
            ObjectDataType("Foo", "pkg", linkedMapOf(
                "foo" to propertyDataTypeString(),
                "fux" to propertyDataTypeString()
            ), constraints = DataTypeConstraints()),
            ObjectDataType(
                "Bar", "pkg", linkedMapOf(
                    "bar" to propertyDataTypeString(),
                    "bux" to propertyDataTypeString()
            ), constraints = null)
        ))

        composed.constraints shouldBe null
    }

})
