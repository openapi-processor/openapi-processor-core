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

})
