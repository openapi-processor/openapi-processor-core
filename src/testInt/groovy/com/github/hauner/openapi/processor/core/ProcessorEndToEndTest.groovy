/*
 * Copyright 2019-2020 the original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.hauner.openapi.processor.core

import io.openapiprocessor.core.parser.ParserType
import com.github.hauner.openapi.processor.core.processor.test.TestProcessor
import com.github.hauner.openapi.test.TestSet
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
