/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.mockk.every
import io.mockk.mockk
import io.openapiprocessor.core.converter.mapping.matcher.TypeMatcher

class TypeMatcherSpec: StringSpec({

    fun createSchema(name: String, format: String?): MappingSchema {
        val info = mockk<MappingSchema>()
        every { info.isPrimitive() } returns false
        every { info.isArray() } returns false
        every { info.getName() } returns name
        every { info.getType() } returns "any"
        every { info.getFormat() } returns format
        return info
    }

    fun createPrimitiveSchema(type: String, format: String?): MappingSchema {
        val info = mockk<MappingSchema>()
        every { info.isPrimitive() } returns true
        every { info.isArray() } returns false
        every { info.getName() } returns "any"
        every { info.getType() } returns type
        every { info.getFormat() } returns format
        return info
    }

    fun createArraySchema(): MappingSchema {
        val info = mockk<MappingSchema>()
        every { info.isPrimitive() } returns false
        every { info.isArray() } returns true
        every { info.getName() } returns "array"
        every { info.getType() } returns "any"
        every { info.getFormat() } returns null
        return info
    }

    "does not match if name differs" {
        val info = createSchema("Name", null)

        val mapping = TypeMapping("other Name", null,"Target")
        val matcher = TypeMatcher(info)

        matcher(mapping).shouldBeFalse()
    }

    "does not match if format differs" {
        val info = createSchema("Name", "a format")

        val mapping = TypeMapping("Name", "other format","Target")
        val matcher = TypeMatcher(info)

        matcher(mapping).shouldBeFalse()
    }

    "matches by name and null format" {
        val info = createSchema("Name", null)

        val mapping = TypeMapping("Name", null,"Target")
        val matcher = TypeMatcher(info)

        matcher(mapping).shouldBeTrue()
    }

    "matches by name and format" {
        val info = createSchema("Name", "format")

        val mapping = TypeMapping("Name", "format","Target")
        val matcher = TypeMatcher(info)

        matcher(mapping).shouldBeTrue()
    }

    "does not match primitive if type differs" {
        val info = createPrimitiveSchema("type", null)

        val mapping = TypeMapping("other type", null,"Target")
        val matcher = TypeMatcher(info)

        matcher(mapping).shouldBeFalse()
    }

    "does not match primitive if format differs" {
        val info = createPrimitiveSchema("type", "a format")

        val mapping = TypeMapping("type", "other format","Target")
        val matcher = TypeMatcher(info)

        matcher(mapping).shouldBeFalse()
    }

    "matches primitive by type and null format" {
        val info = createPrimitiveSchema("type", null)

        val mapping = TypeMapping("type", null,"Target")
        val matcher = TypeMatcher(info)

        matcher(mapping).shouldBeTrue()
    }

    "matches primitive by type and format" {
        val info = createPrimitiveSchema("type", "format")

        val mapping = TypeMapping("type", "format","Target")
        val matcher = TypeMatcher(info)

        matcher(mapping).shouldBeTrue()
    }

    "matches array by name" {
        val info = createArraySchema()

        val mapping = TypeMapping("array", null,"Target")
        val matcher = TypeMatcher(info)

        matcher(mapping).shouldBeTrue()
    }

})
