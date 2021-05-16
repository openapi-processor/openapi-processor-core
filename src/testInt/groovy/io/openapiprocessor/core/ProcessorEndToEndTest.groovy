/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core

import io.openapiprocessor.core.parser.ParserType
import io.openapiprocessor.core.test.TestProcessor
import io.openapiprocessor.test.TestSet
import io.openapiprocessor.test.TestSetRunner
import spock.lang.Specification
import spock.lang.TempDir
import spock.lang.Unroll


/**
 * runs all integration tests.
 */
class ProcessorEndToEndTest extends Specification {

    static def testSets = TestSets.ALL

    static Collection<TestSet> sources () {
        def swagger = testSets.collect {
            new TestSet (name: it, processor: new TestProcessor (), parser: ParserType.SWAGGER.name ())
        }

        def openapi4j = testSets.collect {
            new TestSet (name: it, processor: new TestProcessor (), parser: ParserType.OPENAPI4J.name ())
        }

        swagger + openapi4j
    }

    @TempDir
    public File folder

    @Unroll
    void "native - #testSet"() {
        def runner = new TestSetRunner (testSet)
        def success = runner.runOnNativeFileSystem (folder)

        expect:
        assert success: "** found differences! **"

        where:
        testSet << sources ()
    }

}
