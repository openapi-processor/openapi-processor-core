/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.model.datatypes

import io.kotest.core.spec.style.StringSpec
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe

class ObjectDataTypeSpec : StringSpec({

    "should create import with type name" {
        forAll(row("Foo", "Foo"), row("Fooo", "FoooX")) { id, type ->
            val odt = ObjectDataType(DataTypeName(id, type), "pkg")
            odt.getImports() shouldBe setOf("pkg.$type")
        }
    }

})
