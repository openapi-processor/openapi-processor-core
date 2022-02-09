/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core

import com.google.common.jimfs.Configuration
import com.google.common.jimfs.Jimfs
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.openapiprocessor.core.parser.ParserType
import io.openapiprocessor.test.FileSupport
import io.openapiprocessor.test.TestSet
import io.openapiprocessor.test.TestSetRunner

/**
 * runs integration tests with Jimfs.
 */
class ProcessorEndToEndJimfsSpec: StringSpec({

    for (testSet in sources()) {
        "native - $testSet".config(enabled = true) {
            val support = FileSupport(
                ProcessorPendingSpec::class.java,
                testSet.inputs, testSet.generated)

            TestSetRunner(testSet, support)
            .runOnCustomFileSystem(Jimfs.newFileSystem (Configuration.unix ()))
            .shouldBeTrue()
        }
    }

})

private fun sources(): Collection<TestSet> {

    // the swagger parser does not work with a custom FileSystem

    val openapi4j = ALL_30.map {
        testSet(it.name, ParserType.OPENAPI4J, it.openapi)
    }

    val openapi30 = ALL_30.map {
        testSet(it.name, ParserType.INTERNAL, it.openapi)
    }

    val openapi31 = ALL_31.map {
        testSet(it.name, ParserType.INTERNAL, it.openapi)
    }

    return openapi4j + openapi30 + openapi31
}
