/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.model.datatypes

import io.kotest.core.spec.style.StringSpec
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe

class NullDataTypeSpec: StringSpec ({

    "uses id name and type name of item" {
        val ndt = NullDataType("Null", "pkg",
            ObjectDataType(DataTypeName("Foo", "FooX"), "pkg", linkedMapOf())
        )

        ndt.getName() shouldBe "Null<Foo>"
        ndt.getTypeName() shouldBe "Null<FooX>"
    }

    "should create import with type name" {
        forAll(row("Foo", "Foo"), row("Fooo", "FoooX")) { id, type ->
            val ndt = NullDataType("Null", "pkg",
                ObjectDataType(DataTypeName(id, type), "pkg")
            )
            ndt.getImports() shouldBe setOf(
                "pkg.Null",
                "pkg.$type"
            )
        }
    }

})
