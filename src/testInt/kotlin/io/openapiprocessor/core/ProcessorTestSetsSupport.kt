/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core

import io.openapiprocessor.core.parser.ParserType
import io.openapiprocessor.test.TestSet


const val API_30 = "openapi30.yaml"
const val IN_30 = "inputs30.yaml"

const val API_31 = "openapi31.yaml"
const val IN_31 = "inputs31.yaml"


@Suppress("SameParameterValue")
fun testSet(
    name: String,
    parser: ParserType,
    openapi: String = "openapi.yaml",
    inputs: String = "inputs.yaml",
    generated: String = "generated.yaml"): TestSet {

    val testSet = TestSet()
    testSet.name = name
    testSet.processor = TestProcessor()
    testSet.parser = parser.name
    testSet.openapi = openapi
    testSet.inputs = inputs
    testSet.generated = generated
    return testSet
}
