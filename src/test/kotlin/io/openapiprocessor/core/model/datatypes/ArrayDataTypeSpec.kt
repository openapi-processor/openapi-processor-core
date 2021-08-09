/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.model.datatypes

import io.kotest.core.spec.style.StringSpec
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe

class ArrayDataTypeSpec : StringSpec({

    "array uses id name and type name of item" {
        val array = ArrayDataType(
            ObjectDataType(DataTypeName("Foo", "FooX"), "pkg", linkedMapOf()))

        array.getName() shouldBe "Foo[]"
        array.getTypeName() shouldBe "FooX[]"
    }

    "should create import with type name" {
        forAll(row("Foo", "Foo"), row("Fooo", "FoooX")) { id, type ->
            val dt = ArrayDataType(ObjectDataType(DataTypeName(id, type), "pkg"))
            dt.getImports() shouldBe setOf("pkg.$type")
        }
    }

    "annotates collection and/or model item type" {
        val collection = ArrayDataType(
            ObjectDataType(DataTypeName("Foo", "FooX"), "pkg", linkedMapOf()))

        collection.getTypeName(
            emptySet(),
            emptySet()
        ) shouldBe "FooX[]"

        collection.getTypeName(
            setOf("@One", "@Two"),
            emptySet()
        ) shouldBe "@One @Two FooX[]"

        // can't annotate item of array

        collection.getTypeName(
            emptySet(),
            setOf("@One", "@Two")
        ) shouldBe "FooX[]"

        collection.getTypeName(
            setOf("@One", "@Two"),
            setOf("@OneItem", "@TwoItem")
        ) shouldBe "@One @Two FooX[]"
    }

})
