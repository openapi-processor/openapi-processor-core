/*
 * Copyright © 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.StringSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.openapiprocessor.core.converter.mapping.MappingFinder
import io.openapiprocessor.core.model.DataTypes
import io.openapiprocessor.core.model.HttpMethod
import io.openapiprocessor.core.parser.RefResolver
import io.openapiprocessor.core.parser.Schema

class DataTypeConverterPrimitiveSpec: StringSpec({
    isolationMode = IsolationMode.InstancePerTest

    val any = SchemaInfo.Endpoint("/any", HttpMethod.GET)
    val converter = DataTypeConverter(ApiOptions(), MappingFinder())
    val resolver = mockk<RefResolver>()

    "ignores unknown primitive data type format" {
        forAll(
            row("string","unknown", "String"),
            row("integer","unknown", "Integer"),
            row("number","unknown", "Float"),
            row("boolean","unknown", "Boolean")
        ) { type, format, dataTypeName ->
            val schema = mockk<Schema>(relaxed = true)
            every { schema.getRef() } returns null
            every { schema.getType() } returns type
            every { schema.getFormat() } returns format

            // when:
            val info = SchemaInfo(any, "foo", schema = schema, resolver = resolver)
            val datatype = converter.convert(info, DataTypes())

            // then:
            datatype.getName() shouldBe dataTypeName
        }

    }

})
