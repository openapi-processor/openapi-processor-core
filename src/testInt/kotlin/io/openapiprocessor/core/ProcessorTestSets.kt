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
    TestSet("endpoint-exclude", API_30),
    TestSet("endpoint-http-mapping", API_30), // framework specific
    TestSet("javadoc", API_30),
    TestSet("method-operation-id", API_30),
    TestSet("model-name-suffix", API_30),
    TestSet("object-nullable-properties", API_30),
    TestSet("object-read-write-properties", API_30),
    TestSet("object-without-properties", API_30),
    TestSet("params-additional", API_30),
    TestSet("params-complex-data-types", API_30), // framework specific
    TestSet("params-endpoint", API_30),
    TestSet("params-enum", API_30),
    TestSet("params-path-simple-data-types", API_30), // framework specific
    TestSet("params-request-body", API_30), // framework specific
    TestSet("params-request-body-multipart-form-data", API_30), // framework specific
    TestSet("params-simple-data-types", API_30), // framework specific
)

val ALL_31: List<TestSet> = listOf(
    TestSet("bean-validation", API_31),
    TestSet("bean-validation-allof-required", API_31),
    TestSet("bean-validation-iterable", API_31),
    TestSet("bean-validation-requestbody", API_31),
    TestSet("deprecated", API_31),
    TestSet("endpoint-exclude", API_31),
    TestSet("endpoint-http-mapping", API_31), // framework specific
    TestSet("javadoc", API_31),
    TestSet("method-operation-id", API_31),
    TestSet("model-name-suffix", API_31),
    TestSet("object-nullable-properties", API_31), // todo
    TestSet("object-read-write-properties", API_31),
    TestSet("object-without-properties", API_31),
    TestSet("params-additional", API_31),
    TestSet("params-complex-data-types", API_31), // framework specific
    TestSet("params-endpoint", API_31),
    TestSet("params-enum", API_31),
    TestSet("params-path-simple-data-types", API_31), // framework specific
    TestSet("params-request-body", API_31), // framework specific
    TestSet("params-request-body-multipart-form-data", API_31), // framework specific
    TestSet("params-simple-data-types", API_31), // framework specific

)
