/*
 * Copyright © 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.processor.mapping.v2

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.openapiprocessor.core.converter.mapping.*
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

    "read global 'null' mapping".config(enabled = false) {
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
        val `null` = mappings.first() as NullTypeMapping
        `null`.targetTypeName shouldBe "org.openapitools.jackson.nullable.JsonNullable"
    }

    "read endpoint 'null' mapping" {
        val yaml = """
                   |openapi-processor-mapping: v2
                   |
                   |options:
                   |  package-name: io.openapiprocessor.somewhere
                   | 
                   |map:
                   |  paths:
                   |    /foo:
                   |      null: org.openapitools.jackson.nullable.JsonNullable
                   """.trimMargin()

        // when:
        val mapping = reader.read (yaml)
        val mappings = converter.convert (mapping)

        // then:
        val `null` = mappings.first().getChildMappings().first() as NullTypeMapping
        `null`.targetTypeName shouldBe "org.openapitools.jackson.nullable.JsonNullable"
    }

    "read endpoint 'null' mapping with init value" {
        val yaml = """
                   |openapi-processor-mapping: v2
                   |
                   |options:
                   |  package-name: io.openapiprocessor.somewhere
                   | 
                   |map:
                   |  paths:
                   |    /foo:
                   |      null: org.openapitools.jackson.nullable.JsonNullable = JsonNullable.undefined()
                   """.trimMargin()

        // when:
        val mapping = reader.read (yaml)
        val mappings = converter.convert (mapping)

        // then:
        val `null` = mappings.first().getChildMappings().first() as NullTypeMapping
        `null`.targetTypeName shouldBe "org.openapitools.jackson.nullable.JsonNullable"
    }

    "read additional source type parameter annotation" {
        val yaml = """
                   |openapi-processor-mapping: v2
                   |
                   |options:
                   |  package-name: io.openapiprocessor.somewhere
                   | 
                   |map:
                   |  parameters:
                   |    - type: Foo @ io.openapiprocessor.Annotation
                   """.trimMargin()

        // when:
        val mapping = reader.read (yaml)
        val mappings = converter.convert (mapping)

        // then:
        mappings.size.shouldBe(1)
        val annotation = mappings.first() as ParameterAnnotationTypeMapping
        annotation.sourceTypeName shouldBe "Foo"
        annotation.sourceTypeFormat.shouldBeNull()
        annotation.annotation.type shouldBe "io.openapiprocessor.Annotation"
    }

    "read additional source type parameter annotation of path" {
        val yaml = """
                   |openapi-processor-mapping: v2
                   |
                   |options:
                   |  package-name: io.openapiprocessor.somewhere
                   | 
                   |map:
                   |  paths:
                   |    /foo:
                   |      parameters:
                   |        - type: Foo @ io.openapiprocessor.Annotation
                   """.trimMargin()

        // when:
        val mapping = reader.read (yaml)
        val mappings = converter.convert (mapping)

        // then:
        mappings.size.shouldBe(1)
        val ep = mappings[0] as EndpointTypeMapping

        val annotation = ep.typeMappings.first() as ParameterAnnotationTypeMapping
        annotation.sourceTypeName shouldBe "Foo"
        annotation.sourceTypeFormat.shouldBeNull()
        annotation.annotation.type shouldBe "io.openapiprocessor.Annotation"
    }

    "read additional source type parameter annotation of path with method" {
        val yaml = """
                   |openapi-processor-mapping: v2
                   |
                   |options:
                   |  package-name: io.openapiprocessor.somewhere
                   | 
                   |map:
                   |  paths:
                   |    /foo:
                   |        get:
                   |          parameters:
                   |           - type: Foo @ io.openapiprocessor.Annotation
                   """.trimMargin()

        // when:
        val mapping = reader.read (yaml)
        val mappings = converter.convert (mapping)

        // then:
        mappings.size.shouldBe(2)
        val ep = mappings[1] as EndpointTypeMapping

        val annotation = ep.typeMappings.first() as ParameterAnnotationTypeMapping
        annotation.sourceTypeName shouldBe "Foo"
        annotation.sourceTypeFormat.shouldBeNull()
        annotation.annotation.type shouldBe "io.openapiprocessor.Annotation"
    }
})
