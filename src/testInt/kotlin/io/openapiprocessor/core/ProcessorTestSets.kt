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
    TestSet("bean-validation-list-item-import", API_30),
    TestSet("bean-validation-requestbody", API_30),
    TestSet("deprecated", API_30),
    TestSet("endpoint-exclude", API_30),
    TestSet("endpoint-http-mapping", API_30), // framework specific
    TestSet("javadoc", API_30),
    TestSet("method-operation-id", API_30),
    TestSet("model-name-suffix", API_30),
    TestSet("model-name-suffix-with-package-name", API_30),
    TestSet("object-empty", API_30),
    TestSet("object-nullable-properties", API_30),
    TestSet("object-read-write-properties", API_30),
    TestSet("object-without-properties", API_30),
    TestSet("params-additional", API_30),
    TestSet("params-additional-global", API_30),
    TestSet("params-complex-data-types", API_30), // framework specific
    TestSet("params-endpoint", API_30),
    TestSet("params-enum", API_30),
    TestSet("params-path-simple-data-types", API_30), // framework specific
    TestSet("params-request-body", API_30), // framework specific
    TestSet("params-request-body-multipart-form-data", API_30), // framework specific
    TestSet("params-simple-data-types", API_30), // framework specific
    TestSet("ref-array-items-nested", API_30),
    TestSet("ref-chain-spring-124.1", API_30),
    TestSet("ref-chain-spring-124.2", API_30),
    TestSet("ref-into-another-file", API_30),
    TestSet("ref-into-another-file-path", API_30),
    TestSet("ref-is-relative-to-current-file", API_30),
    TestSet("ref-loop", API_30),
    TestSet("ref-loop-array", API_30),
    TestSet("ref-parameter", API_30),
    TestSet("ref-parameter-with-primitive-mapping", API_30),
    TestSet("ref-to-escaped-path-name", API_30),
    TestSet("response-array-data-type-mapping", API_30),
    TestSet("response-complex-data-types", API_30),
    TestSet("response-content-multiple-no-content", API_30),
    TestSet("response-content-multiple-style-all", API_30),
    TestSet("response-content-multiple-style-success", API_30),
    TestSet("response-content-single", API_30),
    TestSet("response-multi-mapping-with-array-type-mapping", API_30),
    TestSet("response-result-mapping", API_30),
    TestSet("response-simple-data-types", API_30),
    TestSet("response-single-multi-mapping", API_30),
    TestSet("schema-composed", API_30),
    TestSet("schema-composed-allof", API_30),
    TestSet("schema-composed-allof-notype", API_30),
    TestSet("schema-composed-allof-properties", API_30),
    TestSet("schema-composed-allof-ref-sibling", API_30),
    TestSet("schema-composed-nested", API_30),
    TestSet("schema-composed-oneof-interface", API_30)
)

val ALL_31: List<TestSet> = listOf(
    TestSet("bean-validation", API_31),
    TestSet("bean-validation-allof-required", API_31),
    TestSet("bean-validation-iterable", API_31),
    TestSet("bean-validation-list-item-import", API_31),
    TestSet("bean-validation-requestbody", API_31),
    TestSet("deprecated", API_31),
    TestSet("endpoint-exclude", API_31),
    TestSet("endpoint-http-mapping", API_31), // framework specific
    TestSet("javadoc", API_31),
    TestSet("method-operation-id", API_31),
    TestSet("model-name-suffix", API_31),
    TestSet("model-name-suffix-with-package-name", API_31),
    TestSet("object-empty", API_31),
    TestSet("object-nullable-properties", API_31),
    TestSet("object-read-write-properties", API_31),
    TestSet("object-without-properties", API_31),
    TestSet("params-additional", API_31),
    TestSet("params-additional-global", API_31),
    TestSet("params-complex-data-types", API_31), // framework specific
    TestSet("params-endpoint", API_31),
    TestSet("params-enum", API_31),
    TestSet("params-path-simple-data-types", API_31), // framework specific
    TestSet("params-request-body", API_31), // framework specific
    TestSet("params-request-body-multipart-form-data", API_31), // framework specific
    TestSet("params-simple-data-types", API_31), // framework specific
    TestSet("ref-array-items-nested", API_31),
    TestSet("ref-chain-spring-124.1", API_31),
    TestSet("ref-chain-spring-124.2", API_31),
    TestSet("ref-into-another-file", API_31),
    TestSet("ref-into-another-file-path", API_31),
    TestSet("ref-is-relative-to-current-file", API_31),
    TestSet("ref-loop", API_31),
    TestSet("ref-loop-array", API_31),
    TestSet("ref-parameter", API_31),
    TestSet("ref-parameter-with-primitive-mapping", API_31),
    TestSet("ref-to-escaped-path-name", API_31),
    TestSet("response-array-data-type-mapping", API_31),
    TestSet("response-complex-data-types", API_31),
    TestSet("response-content-multiple-no-content", API_31),
    TestSet("response-content-multiple-style-all", API_31),
    TestSet("response-content-multiple-style-success", API_31),
    TestSet("response-content-single", API_31),
    TestSet("response-multi-mapping-with-array-type-mapping", API_31),
    TestSet("response-result-mapping", API_31),
    TestSet("response-simple-data-types", API_31),
    TestSet("response-single-multi-mapping", API_31),
    TestSet("schema-composed", API_31),
    TestSet("schema-composed-allof", API_31),
    TestSet("schema-composed-allof-notype", API_31),
    TestSet("schema-composed-allof-properties", API_31),
    TestSet("schema-composed-allof-ref-sibling", API_31),
    TestSet("schema-composed-nested", API_31),
    TestSet("schema-composed-oneof-interface", API_31)
)
