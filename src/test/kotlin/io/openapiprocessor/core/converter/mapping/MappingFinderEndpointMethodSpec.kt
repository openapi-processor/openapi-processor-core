/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldNotBeEmpty
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

    "endpoint/method parameter mapping matches single mapping" {
        val finder = MappingFinder(
            listOf(
                EndpointTypeMapping("/foo", null, emptyList()),
                EndpointTypeMapping("/foo", HttpMethod.GET, listOf(
                    ParameterTypeMapping("foo param",
                        TypeMapping("Foo", "io.openapiprocessor.Foo")),
                    ParameterTypeMapping("far param",
                        TypeMapping("far", "io.openapiprocessor.Far")),
                    ParameterTypeMapping("bar param",
                        TypeMapping("Bar", "io.openapiprocessor.Bar"))
            )))
        )

        val info = SchemaInfo(foo, "far param", "", null, resolver)
        val result = finder.findEndpointTypeMapping(info)

        result.shouldNotBeNull()
        result.sourceTypeName.shouldBe("far")
        result.targetTypeName.shouldBe("io.openapiprocessor.Far")
    }

    "endpoint/method response mapping matches single mapping" {
        val finder = MappingFinder(
            listOf(
                EndpointTypeMapping("/foo", null, emptyList()),
                EndpointTypeMapping("/foo", HttpMethod.GET, listOf(
                    ResponseTypeMapping("application/json",
                        TypeMapping("Foo", "io.openapiprocessor.Foo")),
                    ResponseTypeMapping("application/json-2",
                        TypeMapping("far", "io.openapiprocessor.Far")),
                    ResponseTypeMapping("application/json-3",
                        TypeMapping("Bar", "io.openapiprocessor.Bar"))
            )))
        )

        val info = SchemaInfo(foo, "", "application/json",null, resolver)
        val result = finder.findEndpointTypeMapping(info)

        result.shouldNotBeNull()
        result.sourceTypeName.shouldBe("Foo")
        result.targetTypeName.shouldBe("io.openapiprocessor.Foo")
    }

    "endpoint type mapping matches null mapping" {
        val finder = MappingFinder(
            listOf(
                EndpointTypeMapping("/foo", null, emptyList()),
                EndpointTypeMapping("/foo", HttpMethod.PATCH, listOf(
                    NullTypeMapping("null", "org.openapitools.jackson.nullable.JsonNullable"),
                    TypeMapping("Far", "io.openapiprocessor.Far"),
                    TypeMapping("Bar", "io.openapiprocessor.Bar")
            )))
        )

        val info = SchemaInfo(
            SchemaInfo.Endpoint("/foo", HttpMethod.PATCH),
            "Foo", "", null, resolver)
        val result = finder.findEndpointNullTypeMapping(info)

        result.shouldNotBeNull()
        result.sourceTypeName.shouldBe("null")
        result.targetTypeName.shouldBe("org.openapitools.jackson.nullable.JsonNullable")
    }

    "endpoint/method add parameter mapping matches" {
        val finder = MappingFinder(
            listOf(
                EndpointTypeMapping("/foo", null, emptyList()),
                EndpointTypeMapping("/foo", HttpMethod.GET, listOf(
                    AddParameterTypeMapping("foo param",
                        TypeMapping(null, "io.openapiprocessor.Foo")),
                    AddParameterTypeMapping("bar param",
                        TypeMapping(null, "io.openapiprocessor.Foo"))
            )))
        )

        val info = SchemaInfo(foo, "", "", null, resolver)
        val result = finder.findEndpointAddParameterTypeMappings(info.getPath(), info.getMethod())

        result.shouldNotBeEmpty()
        result[0].parameterName.shouldBe("foo param")
        result[1].parameterName.shouldBe("bar param")
    }

    "endpoint/method result mapping matches" {
        val finder = MappingFinder(
            listOf(
                EndpointTypeMapping("/foo", null, emptyList()),
                EndpointTypeMapping("/foo", HttpMethod.GET, listOf(
                    ResultTypeMapping("io.openapiprocessor.ResultWrapper")
            )))
        )

        val info = SchemaInfo(foo, "", "", null, resolver)
        val result = finder.findEndpointResultTypeMapping(info)

        result.shouldNotBeNull()
        result.targetTypeName.shouldBe("io.openapiprocessor.ResultWrapper")
    }

    "endpoint/method single mapping matches" {
        val finder = MappingFinder(
            listOf(
                EndpointTypeMapping("/foo", null, emptyList()),
                EndpointTypeMapping("/foo", HttpMethod.GET, listOf(
                    TypeMapping("single", "io.openapiprocessor.SingleWrapper")
            )))
        )

        val info = SchemaInfo(foo, "", "", null, resolver)
        val result = finder.findEndpointSingleTypeMapping(info)

        result.shouldNotBeNull()
        result.targetTypeName.shouldBe("io.openapiprocessor.SingleWrapper")
    }

    "endpoint single mapping matches" {
        val finder = MappingFinder(
            listOf(
                EndpointTypeMapping("/foo", null, listOf(
                    TypeMapping("single", "io.openapiprocessor.SingleWrapper")
            )))
        )

        val info = SchemaInfo(foo, "", "", null, resolver)
        val result = finder.findEndpointSingleTypeMapping(info)

        result.shouldNotBeNull()
        result.targetTypeName.shouldBe("io.openapiprocessor.SingleWrapper")
    }

    "endpoint/method multi mapping matches" {
        val finder = MappingFinder(
            listOf(
                EndpointTypeMapping("/foo", null, emptyList()),
                EndpointTypeMapping("/foo", HttpMethod.GET, listOf(
                    TypeMapping("multi", "io.openapiprocessor.MultiWrapper")
            )))
        )

        val info = SchemaInfo(foo, "", "", null, resolver)
        val result = finder.findEndpointMultiTypeMapping(info)

        result.shouldNotBeNull()
        result.targetTypeName.shouldBe("io.openapiprocessor.MultiWrapper")
    }

    "endpoint multi mapping matches" {
        val finder = MappingFinder(
            listOf(
                EndpointTypeMapping("/foo", null, listOf(
                    TypeMapping("multi", "io.openapiprocessor.MultiWrapper")
            )))
        )

        val info = SchemaInfo(foo, "", "", null, resolver)
        val result = finder.findEndpointMultiTypeMapping(info)

        result.shouldNotBeNull()
        result.targetTypeName.shouldBe("io.openapiprocessor.MultiWrapper")
    }

    "endpoint/method excluded" {
        val finder = MappingFinder(
            listOf(
                EndpointTypeMapping("/foo", null, emptyList()),
                EndpointTypeMapping("/foo", HttpMethod.GET, emptyList(), true)
            )
        )

        val info = SchemaInfo(foo, "", "", null, resolver)
        val result = finder.isExcludedEndpoint(info.getPath(), info.getMethod())

        result.shouldBeTrue()
    }

    "endpoint excluded" {
        val finder = MappingFinder(
            listOf(
                EndpointTypeMapping("/foo", null, emptyList(), true)
            )
        )

        val info = SchemaInfo(foo, "", "", null, resolver)
        val result = finder.isExcludedEndpoint(info.getPath(), info.getMethod())

        result.shouldBeTrue()
    }

})
