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

import com.github.hauner.openapi.core.parser.ParserType
import com.github.hauner.openapi.processor.core.processor.test.TestProcessor
import com.github.hauner.openapi.test.ProcessorTestBase
import com.github.hauner.openapi.test.TestSet
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

/**
 * using Junit so IDEA adds a "<Click to see difference>" when using assertEquals().
 */
@RunWith(Parameterized)
class ProcessorEndToEndTest extends ProcessorTestBase {

    static def testSets = TestSet.testSetsCore + TestSet.testSetsFramework

    static def testSetsX = [
        'bean-validation',
//        'endpoint-exclude',
//        'method-operation-id',
//        'no-response-content',
//        'params-additional',
//        'params-complex-data-types',
//        'params-enum',
//        'params-simple-data-types',
//        'params-request-body',
//        'params-request-body-multipart-form-data',
//        'params-path-simple-data-types',
//        'params-spring-pageable-mapping',
//        'ref-into-another-file',
//        'ref-loop',
//        'response-array-data-type-mapping',
//        'response-complex-data-types',
//        'response-content-multiple',
//        'response-content-single',
//        'response-result-mapping',
//        'response-simple-data-types',
//        'response-single-multi-mapping',
//        'schema-composed'
    ]

    @Parameterized.Parameters(name = "{0}")
    static Collection<TestSet> sources () {
        def swagger = testSets.collect {
           new TestSet (name: it, processor: new TestProcessor(), parser: ParserType.SWAGGER.name ())
        }

        def openapi4j = testSets.collect {
           new TestSet (name: it, processor: new TestProcessor(), parser: ParserType.OPENAPI4J.name ())
        }

        swagger + openapi4j
    }

    ProcessorEndToEndTest (TestSet testSet) {
        super (testSet)
    }

    @Test
    void "native - processor creates expected files for api set "() {
        runOnNativeFileSystem ()
    }

}
