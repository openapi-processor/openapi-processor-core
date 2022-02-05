/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core

data class TestSet(val name: String, val openapi: String)

val ALL_30: List<TestSet> = listOf(
    TestSet("params-additional", API_30)
)

val ALL_31: List<TestSet> = listOf(
    TestSet("params-additional", API_31)
)
