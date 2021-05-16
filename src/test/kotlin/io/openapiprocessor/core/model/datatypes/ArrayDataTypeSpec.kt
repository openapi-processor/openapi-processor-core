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

})
