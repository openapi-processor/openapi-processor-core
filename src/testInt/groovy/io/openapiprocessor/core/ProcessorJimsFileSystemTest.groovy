/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core

import io.openapiprocessor.core.parser.ParserType
import com.google.common.jimfs.Configuration
import com.google.common.jimfs.Jimfs
import io.openapiprocessor.core.test.TestProcessor
import io.openapiprocessor.test.TestSet
import io.openapiprocessor.test.TestSetRunner
import spock.lang.Specification
import spock.lang.Unroll

/**
 * runs all integration tests with Jimfs.
 */
class ProcessorJimsFileSystemTest extends Specification {

    static def testSets = TestSets.ALL

    static Collection<TestSet> sources () {
        // the swagger parser does not work with a custom FileSystem so we just run the test with
        // openapi4j

        testSets.collect {
           new TestSet (name: it, processor: new TestProcessor(), parser: ParserType.OPENAPI4J)
        }
    }

    @Unroll
    void "jimfs - #testSet"() {
        def runner = new TestSetRunner (testSet)
        def success = runner.runOnCustomFileSystem (Jimfs.newFileSystem (Configuration.unix ()))

        expect:
        assert success: "** found differences! **"

        where:
        testSet << sources ()
    }

}
