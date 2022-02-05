/*
 * Copyright © 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.openapiprocessor.core.support.Empty

class OptionsConverterSpec: StringSpec({

    "produces default options if input options are empty" {
        val converter = OptionsConverter()

        val options = converter.convertOptions(emptyMap())

        options.targetDir shouldBe null
        options.packageName shouldBe "io.openapiprocessor.generated"
        options.beanValidation shouldBe false
        options.javadoc shouldBe false
        options.modelNameSuffix shouldBe String.Empty
        options.typeMappings shouldHaveSize 0
        options.formatCode.shouldBeTrue()
    }

    "should set target dir" {
        val converter = OptionsConverter()

        val options = converter.convertOptions(mapOf(
            "targetDir" to "generated target dir"
        ))

        options.targetDir shouldBe "generated target dir"
    }

    "should accept deprecated packageName map option" {
        val converter = OptionsConverter(true)

        val options = converter.convertOptions(mapOf(
            "packageName" to "obsolete"
        ))

        options.packageName shouldBe "obsolete"
    }

    "should accept deprecated beanValidation map option" {
        val converter = OptionsConverter(true)

        val options = converter.convertOptions(mapOf(
            "beanValidation" to true
        ))

        options.beanValidation shouldBe true
    }

    "should accept deprecated typeMappings map option" {
        val converter = OptionsConverter(true)

        val options = converter.convertOptions(mapOf(
            "typeMappings" to """
                openapi-processor-mapping: v2
                options:
                  package-name: generated
            """.trimIndent()
        ))

        options.packageName shouldBe "generated"
    }

    "should read Mapping options V1" {
        val converter = OptionsConverter()
        val options = converter.convertOptions(mapOf(
            "mapping" to """
                options:
                  package-name: generated
                  bean-validation: true
            """.trimIndent()
        ))

        options.packageName shouldBe "generated"
        options.beanValidation shouldBe true
    }

    "should read Mapping options V2" {
        val converter = OptionsConverter()
        val options = converter.convertOptions(mapOf(
            "mapping" to """
                openapi-processor-mapping: v2
                options:
                  package-name: generated
                  model-name-suffix: Suffix
                  bean-validation: true
                  javadoc: true
                  format-code: false
            """.trimIndent()
        ))

        options.packageName shouldBe "generated"
        options.modelNameSuffix shouldBe "Suffix"
        options.beanValidation shouldBe true
        options.javadoc shouldBe true
        options.formatCode.shouldBeFalse()
    }

})
