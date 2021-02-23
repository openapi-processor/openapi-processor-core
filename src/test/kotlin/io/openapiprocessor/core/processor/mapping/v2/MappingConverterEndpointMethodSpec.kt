/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.processor.mapping.v2

import io.kotest.core.spec.style.StringSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.openapiprocessor.core.converter.mapping.EndpointTypeMapping
import io.openapiprocessor.core.converter.mapping.TypeMapping
import io.openapiprocessor.core.model.HttpMethod
import io.openapiprocessor.core.processor.MappingConverter
import io.openapiprocessor.core.processor.MappingReader

class MappingConverterEndpointMethodSpec: StringSpec({

    val reader = MappingReader()
    val converter = MappingConverter()

    "reads endpoint method type mapping" {
        val yaml = """
                   |openapi-processor-mapping: v2
                   | 
                   |map:
                   |  paths:
                   |    /foo:
                   |      get:
                   |        types:
                   |         - type: Foo => io.openapiprocessor.Foo
                   """.trimMargin()

        // when:
        val mapping = reader.read (yaml)
        val mappings = converter.convert (mapping)

        // then:
        mappings.size.shouldBe(2)
        val epAll = mappings[0] as EndpointTypeMapping
        epAll.path.shouldBe("/foo")
        epAll.method.shouldBeNull()
        epAll.getChildMappings().isEmpty().shouldBeTrue()

        val ep = mappings[1] as EndpointTypeMapping
        ep.path.shouldBe("/foo")
        ep.method.shouldBe(HttpMethod.GET)
        ep.getChildMappings().first().shouldBeInstanceOf<TypeMapping>()
    }

    "reads any endpoint method type mappings" {
        forAll(
            row(HttpMethod.GET),
            row(HttpMethod.PUT),
            row(HttpMethod.POST),
            row(HttpMethod.DELETE),
            row(HttpMethod.OPTIONS),
            row(HttpMethod.HEAD),
            row(HttpMethod.PATCH),
            row(HttpMethod.TRACE)
        ) { method ->
            val yaml = """
                       |openapi-processor-mapping: v2
                       | 
                       |map:
                       |  paths:
                       |    /foo:
                       |      ${method.method}:
                       |        types:
                       |         - type: Foo => io.openapiprocessor.Foo
                       """.trimMargin()

            // when:
            val mapping = reader.read (yaml)
            val mappings = converter.convert (mapping)

            // then:
            mappings.size.shouldBe(2)
            val epAll = mappings[0] as EndpointTypeMapping
            epAll.path.shouldBe("/foo")
            epAll.method.shouldBeNull()
            epAll.getChildMappings().isEmpty().shouldBeTrue()

            val ep = mappings[1] as EndpointTypeMapping
            ep.path.shouldBe("/foo")
            ep.method.shouldBe(method)
            ep.getChildMappings().first().shouldBeInstanceOf<TypeMapping>()
        }
    }

})
