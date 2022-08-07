/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.processor.mapping.v2


import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.maps.shouldBeEmpty
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.openapiprocessor.core.processor.mapping.v2.parser.MappingParser
import io.openapiprocessor.core.processor.mapping.v2.parser.Mapping.Kind.MAP


class JccParserSpec: StringSpec ({

    "map source type to fully qualified java target type" {
        val source = "SourceType => io.oap.TargetType"

        val mapping = MappingParser(source).mapping()
        mapping.kind shouldBe MAP
        mapping.sourceType shouldBe "SourceType"
        mapping.targetType shouldBe "io.oap.TargetType"
        mapping.targetGenericTypes.shouldBeEmpty()
        mapping.annotationType.shouldBeNull()
        mapping.annotationParameters.shouldBeEmpty()
    }

    "map source type to fully qualified java target type with generic parameters" {
        val source = "SourceType => io.oap.TargetType <java.lang.String, java.lang.Integer>"

        val mapping = MappingParser(source).mapping()
        mapping.kind shouldBe MAP
        mapping.sourceType shouldBe "SourceType"
        mapping.targetType shouldBe "io.oap.TargetType"
        mapping.targetGenericTypes shouldBe listOf("java.lang.String", "java.lang.Integer")
        mapping.annotationType.shouldBeNull()
        mapping.annotationParameters.shouldBeEmpty()
    }

    "map source type to fully qualified java target type with annotation" {
        val source = "SourceType => io.oap.Annotation io.oap.TargetType"

        val mapping = MappingParser(source).mapping()
        mapping.kind shouldBe MAP
        mapping.sourceType shouldBe "SourceType"
        mapping.targetType shouldBe "io.oap.TargetType"
        mapping.targetGenericTypes.shouldBeEmpty()
        mapping.annotationType shouldBe "io.oap.Annotation"
        mapping.annotationParameters.shouldBeEmpty()
    }

    "map source type to fully qualified java target type with annotation & default simple parameter" {
        val source = "SourceType => io.oap.Annotation(42) io.oap.TargetType"

        val mapping = MappingParser(source).mapping()
        mapping.kind shouldBe MAP
        mapping.sourceType shouldBe "SourceType"
        mapping.targetType shouldBe "io.oap.TargetType"
        mapping.targetGenericTypes.shouldBeEmpty()
        mapping.annotationType shouldBe "io.oap.Annotation"
        mapping.annotationParameters.size shouldBe 1
        mapping.annotationParameters[""] shouldBe "42"
    }

    "map source type to fully qualified java target type with annotation & default string parameter" {
        val source = """SourceType => io.oap.Annotation("42") io.oap.TargetType"""

        val mapping = MappingParser(source).mapping()
        mapping.kind shouldBe MAP
        mapping.sourceType shouldBe "SourceType"
        mapping.targetType shouldBe "io.oap.TargetType"
        mapping.targetGenericTypes.shouldBeEmpty()
        mapping.annotationType shouldBe "io.oap.Annotation"
        mapping.annotationParameters.size shouldBe 1
        mapping.annotationParameters[""] shouldBe """"42""""
    }

    "map source type to fully qualified java target type with annotation & multiple named parameters" {
        val source = """SourceType => io.oap.Annotation(a = 42, bb = "foo") io.oap.TargetType"""

        val mapping = MappingParser(source).mapping()
        mapping.kind shouldBe MAP
        mapping.sourceType shouldBe "SourceType"
        mapping.targetType shouldBe "io.oap.TargetType"
        mapping.targetGenericTypes.shouldBeEmpty()
        mapping.annotationType shouldBe "io.oap.Annotation"
        mapping.annotationParameters.size shouldBe 2
        mapping.annotationParameters["a"] shouldBe "42"
        mapping.annotationParameters["bb"] shouldBe """"foo""""
    }
})
