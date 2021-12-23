/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core

import io.kotest.core.spec.style.StringSpec
import io.kotest.engine.spec.tempdir
import io.kotest.matchers.booleans.shouldBeTrue
import io.openapiprocessor.api.v1.OpenApiProcessor
import io.openapiprocessor.core.parser.ParserType
import io.openapiprocessor.test.FileSupport
import io.openapiprocessor.test.TestSet
import io.openapiprocessor.test.TestSetRunner

/**
 * helper to run selected integration tests.
 */
class ProcessorPendingSpec: StringSpec({
    val folder = tempdir()

    for (testSet in sources()) {
        "native - $testSet".config(enabled = false) {
            TestSetRunner(testSet, FileSupport(ProcessorPendingSpec::class.java))
                .runOnNativeFileSystem(folder)
                .shouldBeTrue()
        }
    }
})

private fun sources(): Collection<TestSet> {
    return listOf(
        testSet("javadoc", TestProcessor(), ParserType.SWAGGER),
        testSet("javadoc", TestProcessor(), ParserType.OPENAPI4J)
    )
}

@Suppress("SameParameterValue")
private fun testSet(name: String, processor: OpenApiProcessor, parser: ParserType): TestSet {
    val testSet = TestSet()
    testSet.name = name
    testSet.processor = processor
    testSet.parser = parser.name
    return testSet
}
