/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import io.openapiprocessor.core.converter.SchemaInfo
import io.openapiprocessor.core.parser.RefResolver

class MappingFinderSpec: StringSpec({
    val resolver = mockk<RefResolver>()

    "no type mapping in empty mappings" {
        val finder = MappingFinder(emptyList())

        val info = SchemaInfo("/any", "Any", "", null, resolver)
        val result = finder.findTypeMapping(info)

        result.shouldBeNull()
    }

    "type mapping matches single mapping" {
        val finder = MappingFinder(
            listOf(
                TypeMapping("Foo", "io.openapiprocessor.Foo"),
                TypeMapping("Far", "io.openapiprocessor.Far"),
                TypeMapping("Bar", "io.openapiprocessor.Bar")
            )
        )

        val info = SchemaInfo("/any", "Foo", "", null, resolver)
        val result = finder.findTypeMapping(info)

        result.shouldNotBeNull()
        result.sourceTypeName.shouldBe("Foo")
        result.targetTypeName.shouldBe("io.openapiprocessor.Foo")
    }

    "throws on duplicate type mapping" {
        val finder = MappingFinder(
            listOf(
                TypeMapping("Foo", "io.openapiprocessor.Foo"),
                TypeMapping("Foo", "io.openapiprocessor.Foo")
            )
        )

        val info = SchemaInfo("/any", "Foo", "", null, resolver)

        shouldThrow<AmbiguousTypeMappingException> {
            finder.findTypeMapping(info)
        }
    }

    "no io mapping in empty mappings" {
        val finder = MappingFinder(emptyList())

        val param = SchemaInfo("/any", "parameter", "", null, resolver)
        val paramResult = finder.findIoTypeMapping(param)
        paramResult.shouldBeNull()

        val response = SchemaInfo("/any", "", "application/json", null, resolver)
        val responseResult = finder.findIoTypeMapping(response)
        responseResult.shouldBeNull()
    }

    "io parameter mapping matches single mapping" {
        val finder = MappingFinder(
            listOf(
                ParameterTypeMapping("foo param",
                    TypeMapping("Foo", "io.openapiprocessor.Foo")),
                ParameterTypeMapping("far param",
                    TypeMapping("far", "io.openapiprocessor.Far")),
                ParameterTypeMapping("bar param",
                    TypeMapping("Bar", "io.openapiprocessor.Bar"))
            )
        )

        val info = SchemaInfo("/any", "far param", "", null, resolver)
        val result = finder.findIoTypeMapping(info)

        result.shouldNotBeNull()
        result.sourceTypeName.shouldBe("far")
        result.targetTypeName.shouldBe("io.openapiprocessor.Far")
    }

    "io response mapping matches single mapping" {
        val finder = MappingFinder(
            listOf(
                ResponseTypeMapping("application/json",
                    TypeMapping("Foo", "io.openapiprocessor.Foo")),
                ResponseTypeMapping("application/json-2",
                    TypeMapping("far", "io.openapiprocessor.Far")),
                ResponseTypeMapping("application/json-3",
                    TypeMapping("Bar", "io.openapiprocessor.Bar"))
            )
        )

        val info = SchemaInfo("/any", "", "application/json",null, resolver)
        val result = finder.findIoTypeMapping(info)

        result.shouldNotBeNull()
        result.sourceTypeName.shouldBe("Foo")
        result.targetTypeName.shouldBe("io.openapiprocessor.Foo")
    }

    "throws on duplicate parameter mapping" {
        val finder = MappingFinder(
            listOf(
                ParameterTypeMapping("foo param",
                    TypeMapping("Foo A", "io.openapiprocessor.Foo A")),
                ParameterTypeMapping("foo param",
                    TypeMapping("Foo B", "io.openapiprocessor.Foo B"))
            )
        )

        val info = SchemaInfo("/any", "foo param", "", null, resolver)

        shouldThrow<AmbiguousTypeMappingException> {
            finder.findIoTypeMapping(info)
        }
    }

    "throws on duplicate response mapping" {
        val finder = MappingFinder(
            listOf(
                ResponseTypeMapping("application/json",
                    TypeMapping("Foo", "io.openapiprocessor.Foo")),
                ResponseTypeMapping("application/json",
                    TypeMapping("far", "io.openapiprocessor.Far"))
            )
        )

        val info = SchemaInfo("/any", "", "application/json", null, resolver)

        shouldThrow<AmbiguousTypeMappingException> {
            finder.findIoTypeMapping(info)
        }
    }

    "no endpoint type mapping in empty mappings" {
        val finder = MappingFinder(emptyList())

        val info = SchemaInfo("/foo", "Foo", "", null, resolver)
        val result = finder.findEndpointTypeMapping(info)

        result.shouldBeNull()
    }

    "endpoint parameter mapping matches single mapping" {
        val finder = MappingFinder(
            listOf(
                EndpointTypeMapping("/foo", listOf(
                    ParameterTypeMapping("foo param",
                        TypeMapping("Foo", "io.openapiprocessor.Foo")),
                    ParameterTypeMapping("far param",
                        TypeMapping("far", "io.openapiprocessor.Far")),
                    ParameterTypeMapping("bar param",
                        TypeMapping("Bar", "io.openapiprocessor.Bar"))
            )))
        )

        val info = SchemaInfo("/foo", "far param", "", null, resolver)
        val result = finder.findEndpointTypeMapping(info)

        result.shouldNotBeNull()
        result.sourceTypeName.shouldBe("far")
        result.targetTypeName.shouldBe("io.openapiprocessor.Far")
    }

    "endpoint response mapping matches single mapping" {
        val finder = MappingFinder(
            listOf(
                EndpointTypeMapping("/foo", listOf(
                    ResponseTypeMapping("application/json",
                        TypeMapping("Foo", "io.openapiprocessor.Foo")),
                     ResponseTypeMapping("application/json-2",
                        TypeMapping("far", "io.openapiprocessor.Far")),
                    ResponseTypeMapping("application/json-3",
                        TypeMapping("Bar", "io.openapiprocessor.Bar"))
            )))
        )

        val info = SchemaInfo("/foo", "", "application/json",null, resolver)
        val result = finder.findEndpointTypeMapping(info)

        result.shouldNotBeNull()
        result.sourceTypeName.shouldBe("Foo")
        result.targetTypeName.shouldBe("io.openapiprocessor.Foo")
    }

    "throws on duplicate endpoint parameter mapping" {
        val finder = MappingFinder(listOf(
            EndpointTypeMapping("/foo", listOf(
                    ParameterTypeMapping("foo param",
                        TypeMapping("Foo A", "io.openapiprocessor.Foo A")),
                    ParameterTypeMapping("foo param",
                        TypeMapping("Foo B", "io.openapiprocessor.Foo B"))
                )))
        )

        val info = SchemaInfo("/foo", "foo param", "", null, resolver)

        shouldThrow<AmbiguousTypeMappingException> {
            finder.findEndpointTypeMapping(info)
        }
    }

    "throws on duplicate endpoint response mapping" {
        val finder = MappingFinder(
            listOf(
                EndpointTypeMapping("/foo", listOf(
                    ResponseTypeMapping("application/json",
                        TypeMapping("Foo", "io.openapiprocessor.Foo")),
                    ResponseTypeMapping("application/json",
                        TypeMapping("far", "io.openapiprocessor.Far"))
            )))
        )

        val info = SchemaInfo("/foo", "", "application/json", null, resolver)

        shouldThrow<AmbiguousTypeMappingException> {
            finder.findEndpointTypeMapping(info)
        }
    }

    "endpoint type mapping matches single mapping" {
        val finder = MappingFinder(
            listOf(
                EndpointTypeMapping("/foo", listOf(
                    TypeMapping("Foo", "io.openapiprocessor.Foo"),
                    TypeMapping("Far", "io.openapiprocessor.Far"),
                    TypeMapping("Bar", "io.openapiprocessor.Bar")
            )))
        )

        val info = SchemaInfo("/foo", "Foo", "", null, resolver)
        val result = finder.findEndpointTypeMapping(info)

        result.shouldNotBeNull()
        result.sourceTypeName.shouldBe("Foo")
        result.targetTypeName.shouldBe("io.openapiprocessor.Foo")
    }

})
