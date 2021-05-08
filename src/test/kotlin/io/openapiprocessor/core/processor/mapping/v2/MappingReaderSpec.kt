/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */
package io.openapiprocessor.core.processor.mapping.v2

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.openapiprocessor.core.processor.MappingReader
import io.openapiprocessor.core.processor.MappingValidator
import org.slf4j.Logger

class MappingReaderSpec: StringSpec ({
    isolationMode = IsolationMode.InstancePerTest

    "warns use of old mapping version key" {
        val yaml = """
            |openapi-processor-spring: v2
        """.trimMargin()

        val validator = mockk<MappingValidator>()
        every { validator.validate(any()) } returns emptySet()

        val log = mockk<Logger>(relaxed = true)

        val reader = MappingReader(validator)
        reader.log = log

        // when:
        reader.read(yaml)

        // then:
        verify(exactly = 1) { log.warn(any()) }
    }

    "validates mapping.yaml" {
        val yaml = """
            |openapi-processor-mapping: v2
            |
            |options:
            |  package-name: io.openapiprocessor.somewhere
        """.trimMargin()

        val validator = mockk<MappingValidator>()
        every { validator.validate(any()) } returns emptySet()

        MappingReader(validator).read(yaml)

        verify { validator.validate(yaml) }
    }

    "logs mapping.yaml validation errors" {
        val yaml = """
            |openapi-processor-mapping: v2
        """.trimMargin()

        val log = mockk<Logger>(relaxed = true)

        val reader = MappingReader()
        reader.log = log

        // when:
        reader.read (yaml)

        // then:
        verify(exactly = 1) { log.warn("\$.options: is missing but it is required") }
    }

    "reads model-name-suffix" {
        val yaml = """
            |openapi-processor-mapping: v2
            |options:
            |  model-name-suffix: Suffix
        """.trimMargin()

        val reader = MappingReader()

        // when:
        val mapping = reader.read (yaml) as Mapping

        // then:
        mapping.options.modelNameSuffix shouldBe "Suffix"
    }

})
