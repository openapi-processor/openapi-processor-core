/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core

class TestSets {

    static def ALL = [
        'bean-validation',
        'bean-validation-allof-required',
        'bean-validation-iterable',
        'bean-validation-requestbody',
        'deprecated',
        'endpoint-exclude',
        'endpoint-http-mapping',                    // framework specific
        'javadoc',
        'method-operation-id',
        'model-name-suffix',
        'object-nullable-properties',
        'object-without-properties',
        'params-additional',
        'params-complex-data-types',                // framework specific
        'params-endpoint',
        'params-enum',
        'params-path-simple-data-types',            // framework specific
        'params-request-body',                      // framework specific
        'params-request-body-multipart-form-data',  // framework specific
        'params-simple-data-types',                 // framework specific
        'ref-array-items-nested',
        'ref-chain-spring-124.1',
        'ref-chain-spring-124.2',
        'ref-into-another-file',
        'ref-into-another-file-path',
        'ref-is-relative-to-current-file',
        'ref-loop',
        'ref-loop-array',
        'ref-parameter',
        'ref-parameter-with-primitive-mapping',
        'ref-to-escaped-path-name',
        'response-array-data-type-mapping',
        'response-complex-data-types',
        'response-content-multiple-no-content',
        'response-content-multiple-style-all',
        'response-content-multiple-style-success',
        'response-content-single',
        'response-result-mapping',
        'response-simple-data-types',
        'response-single-multi-mapping',
        'schema-composed',
        'schema-composed-allof',
        'schema-composed-allof-notype'
    ]

}
