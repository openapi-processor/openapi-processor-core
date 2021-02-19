/*
 * Copyright © 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.processor.mapping.v2

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.openapiprocessor.core.converter.mapping.TypeMapping
import io.openapiprocessor.core.processor.MappingConverter
import io.openapiprocessor.core.processor.MappingReader

class MappingConverterSpec: StringSpec({
    isolationMode = IsolationMode.InstancePerTest

    val reader = MappingReader()
    val converter = MappingConverter()

    "read generic parameter with generated package ref" {
        val yaml = """
                   |openapi-processor-mapping: v2
                   |
                   |options:
                   |  package-name: io.openapiprocessor.somewhere
                   | 
                   |map:
                   |  types:
                   |    - type: Foo => io.openapiprocessor.Foo<{package-name}.Bar>
                   """.trimMargin()

        // when:
        val mapping = reader.read (yaml)
        val mappings = converter.convert (mapping)

        // then:
        val type = mappings.first() as TypeMapping
        type.targetTypeName shouldBe "io.openapiprocessor.Foo"
        type.genericTypeNames shouldBe listOf("io.openapiprocessor.somewhere.Bar")
    }

    "read 'null' mapping" {
        val yaml = """
                   |openapi-processor-mapping: v2
                   |
                   |options:
                   |  package-name: io.openapiprocessor.somewhere
                   | 
                   |map:
                   |  null: org.openapitools.jackson.nullable.JsonNullable
                   """.trimMargin()

        // when:
        val mapping = reader.read (yaml)
        val mappings = converter.convert (mapping)

        // then:
        val `null` = mappings.first() as TypeMapping
        `null`.targetTypeName shouldBe "org.openapitools.jackson.nullable.JsonNullable"
    }

})
