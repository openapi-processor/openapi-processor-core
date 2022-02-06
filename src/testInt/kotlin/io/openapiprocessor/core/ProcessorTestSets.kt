/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core

data class TestSet(val name: String, val openapi: String)

val ALL_30: List<TestSet> = listOf(
    TestSet("bean-validation", API_30),
    TestSet("bean-validation-allof-required", API_30),
    TestSet("bean-validation-iterable", API_30),
    TestSet("bean-validation-requestbody", API_30),
    TestSet("deprecated", API_30),
    TestSet("params-additional", API_30)
)

val ALL_31: List<TestSet> = listOf(
    TestSet("bean-validation", API_31),
    TestSet("bean-validation-allof-required", API_31),
    TestSet("bean-validation-iterable", API_31),
    TestSet("bean-validation-requestbody", API_31),
    TestSet("deprecated", API_31),
    TestSet("params-additional", API_31)
)
