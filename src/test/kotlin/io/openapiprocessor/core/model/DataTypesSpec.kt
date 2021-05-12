/*
 * Copyright © 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.model

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.openapiprocessor.core.model.datatypes.DataTypeName
import io.openapiprocessor.core.model.datatypes.ObjectDataType
import io.openapiprocessor.core.model.datatypes.StringDataType
import io.openapiprocessor.core.model.datatypes.StringEnumDataType

class DataTypesSpec : StringSpec({

    val dataTypes = DataTypes()

    "does not provide unused model data types" {
        dataTypes.add(ObjectDataType("Foo", "any", linkedMapOf(
            Pair("foo", StringDataType())
        )))

        dataTypes.getModelDataTypes().size shouldBe 0
    }

    "does provide used model data types" {
        dataTypes.add(ObjectDataType("Foo", "any", linkedMapOf(
            Pair("foo", StringDataType())
        )))
        dataTypes.addRef("Foo")

        dataTypes.getModelDataTypes().size shouldBe 1
    }

    "does not provide unused enum data types" {
        dataTypes.add(StringEnumDataType(DataTypeName("Foo"), "any"))

        dataTypes.getEnumDataTypes().size shouldBe 0
    }

    "does provide used enum data types" {
        dataTypes.add(StringEnumDataType(DataTypeName("Foo"), "any"))
        dataTypes.addRef("Foo")

        dataTypes.getEnumDataTypes().size shouldBe 1
    }

})
