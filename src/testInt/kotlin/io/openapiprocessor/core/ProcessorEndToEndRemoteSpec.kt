/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core

import io.kotest.core.spec.style.StringSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.engine.spec.tempdir
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldContainAll
import io.openapiprocessor.test.FileSupport
import java.nio.file.Path


class ProcessorEndToEndRemoteSpec: StringSpec({
    val repo = "https://raw.githubusercontent.com/openapi-processor/openapi-processor-core/master"

    val pkg = "generated"
    val mapping = """
        openapi-processor-mapping: v2
    
        options:
          package-name: $pkg
    """.trimIndent()

    "processes remote openapi with ref's" {
        forAll(
            row("INTERNAL", "ref-into-another-file", "openapi30.yaml"),
            row("INTERNAL", "ref-into-another-file", "openapi31.yaml"),
            row("SWAGGER", "ref-into-another-file", "openapi30.yaml"),
            row("OPENAPI4J", "ref-into-another-file", "openapi30.yaml")
        ) { parser, source, api ->
            val folder = tempdir()

            val processor = TestProcessor()
            processor.run(mutableMapOf(
                "apiPath" to "$repo/src/testInt/resources/tests/$source/inputs/$api",
                "targetDir" to folder.canonicalPath,
                "parser" to parser,
                "mapping" to mapping
            ))

            val sourcePath = "/tests/$source"
            val expectedPath = "$sourcePath/$pkg"
            val generatedPath = Path.of (folder.canonicalPath).resolve (pkg)

            val files = FileSupport(ProcessorEndToEndRemoteSpec::class.java)
            val expectedFiles = files.collectRelativeOutputPaths (sourcePath, pkg)
            val generatedFiles = FileSupport.collectPaths (generatedPath)

            generatedFiles.shouldContainAll(expectedFiles)

            var success = true
            expectedFiles.forEach {
                val expected = "$expectedPath/$it"
                val generated = generatedPath.resolve (it)

                val hasDiff = !files.printUnifiedDiff (expected, generated)

                success = success && hasDiff
            }

            success.shouldBeTrue()
        }
    }

})
