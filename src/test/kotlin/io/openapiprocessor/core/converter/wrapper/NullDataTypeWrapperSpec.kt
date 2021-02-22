/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.wrapper

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.mockk.every
import io.mockk.mockk
import io.openapiprocessor.core.converter.ApiOptions
import io.openapiprocessor.core.converter.SchemaInfo
import io.openapiprocessor.core.converter.mapping.MappingFinder
import io.openapiprocessor.core.converter.mapping.NullTypeMapping
import io.openapiprocessor.core.model.HttpMethod
import io.openapiprocessor.core.model.datatypes.StringDataType
import io.openapiprocessor.core.parser.RefResolver

class NullDataTypeWrapperSpec : StringSpec({
    val resolver = mockk<RefResolver>()
    val any = SchemaInfo.Endpoint("/any", HttpMethod.GET)

    "does not wrap datatype if there is no null mapping" {
        val finder = mockk<MappingFinder>()
        every { finder.findEndpointNullTypeMapping(any()) } returns null

        val wrapper = NullDataTypeWrapper(ApiOptions(), finder)

        val info = SchemaInfo(any, "", "", null, resolver)
        val dataType = StringDataType()

        wrapper.wrap(dataType, info).shouldBeSameInstanceAs(dataType)
    }

    "does wrap datatype if there is a null mapping" {
        val finder = mockk<MappingFinder>()
        every { finder.findEndpointNullTypeMapping(any()) } returns NullTypeMapping(
            "null", "org.openapitools.jackson.nullable.JsonNullable")

        val wrapper = NullDataTypeWrapper(ApiOptions(), finder)

        val info = SchemaInfo(any, "", "", null, resolver)
        val dataType = StringDataType()

        val result = wrapper.wrap(dataType, info)
        result.getName().shouldBe("JsonNullable<String>")
        result.getPackageName().shouldBe("org.openapitools.jackson.nullable")
    }

})
