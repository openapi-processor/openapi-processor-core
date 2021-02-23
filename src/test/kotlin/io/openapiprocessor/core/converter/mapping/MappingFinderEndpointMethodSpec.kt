/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import io.openapiprocessor.core.converter.SchemaInfo
import io.openapiprocessor.core.model.HttpMethod
import io.openapiprocessor.core.parser.RefResolver

class MappingFinderEndpointMethodSpec: StringSpec({
    val resolver = mockk<RefResolver>()
    val foo = SchemaInfo.Endpoint("/foo", HttpMethod.GET)

    "endpoint/method type mapping matches single mapping" {
        val finder = MappingFinder(
            listOf(
                EndpointTypeMapping("/foo", null, emptyList()),
                EndpointTypeMapping("/foo", HttpMethod.GET, listOf(
                    TypeMapping("Foo", "io.openapiprocessor.Foo"),
                    TypeMapping("Far", "io.openapiprocessor.Far"),
                    TypeMapping("Bar", "io.openapiprocessor.Bar")
            )))
        )

        val info = SchemaInfo(foo, "Foo", "", null, resolver)
        val result = finder.findEndpointTypeMapping(info)

        result.shouldNotBeNull()
        result.sourceTypeName.shouldBe("Foo")
        result.targetTypeName.shouldBe("io.openapiprocessor.Foo")
    }

})
