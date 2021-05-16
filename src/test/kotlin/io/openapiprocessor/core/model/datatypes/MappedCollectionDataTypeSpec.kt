/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.model.datatypes

import io.kotest.core.spec.style.StringSpec
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe

class MappedCollectionDataTypeSpec : StringSpec({

    "collection uses id name and type name of item" {
        val collection = MappedCollectionDataType("List", "java",
            ObjectDataType(DataTypeName("Foo", "FooX"), "pkg", linkedMapOf()))

        collection.getName() shouldBe "List<Foo>"
        collection.getTypeName() shouldBe "List<FooX>"
    }

    "should create import with type name" {
        forAll(row("Foo", "Foo"), row("Fooo", "FoooX")) { id, type ->
            val dt = MappedCollectionDataType("List", "java",
                ObjectDataType(DataTypeName(id, type), "pkg"))
            dt.getImports() shouldBe setOf(
                "java.List",
                "pkg.$type"
            )
        }
    }

})
