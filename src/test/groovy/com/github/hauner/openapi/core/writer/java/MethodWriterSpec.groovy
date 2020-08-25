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

package com.github.hauner.openapi.core.writer.java

import com.github.hauner.openapi.core.converter.ApiOptions
import io.openapiprocessor.core.model.datatypes.NoneDataType
import io.openapiprocessor.core.model.datatypes.ResultDataType
import com.github.hauner.openapi.core.model.parameters.Parameter
import com.github.hauner.openapi.core.model.parameters.ParameterBase
import com.github.hauner.openapi.core.model.Endpoint
import com.github.hauner.openapi.core.model.Response
import io.openapiprocessor.core.model.datatypes.ArrayDataType
import io.openapiprocessor.core.model.datatypes.BooleanDataType
import io.openapiprocessor.core.model.datatypes.DataType
import io.openapiprocessor.core.model.datatypes.DoubleDataType
import io.openapiprocessor.core.model.datatypes.FloatDataType
import io.openapiprocessor.core.model.datatypes.IntegerDataType
import io.openapiprocessor.core.model.datatypes.LongDataType
import io.openapiprocessor.core.model.datatypes.MappedCollectionDataType
import io.openapiprocessor.core.model.datatypes.ObjectDataType
import io.openapiprocessor.core.model.datatypes.StringDataType
import com.github.hauner.openapi.core.model.parameters.QueryParameter
import com.github.hauner.openapi.core.test.EmptyResponse
import com.github.hauner.openapi.core.test.TestMappingAnnotationWriter
import com.github.hauner.openapi.core.test.TestParameterAnnotationWriter
import io.openapiprocessor.core.model.HttpMethod
import spock.lang.Specification
import spock.lang.Unroll

import static io.openapiprocessor.core.model.Builder.endpoint

class MethodWriterSpec extends Specification {
    def apiOptions = new ApiOptions()

    def writer = new MethodWriter (
        apiOptions: apiOptions,
        mappingAnnotationWriter: new TestMappingAnnotationWriter(),
        parameterAnnotationWriter: new TestParameterAnnotationWriter())
    def target = new StringWriter ()

    @Deprecated // use endpoint() builder
    private Endpoint createEndpoint (Map properties) {
        new Endpoint(properties).initEndpointResponses ()
    }

    void "writes mapping annotation" () {
        def endpoint = createEndpoint (path: '/foo', method: HttpMethod.GET, responses: [
            '204': [new EmptyResponse ()]
        ])

        when:
        writer.write (target, endpoint, endpoint.endpointResponses.first ())

        then:
        target.toString () == """\
    @CoreMapping
    void getFoo();
"""
    }

    void "writes @Deprecated annotation" () {
        def endpoint = endpoint('/foo') {
            deprecated ()

            responses ('204') {
                empty ()
            }
        }

        when:
        writer.write (target, endpoint, endpoint.endpointResponses.first ())

        then:
        target.toString () == """\
    @Deprecated
    @CoreMapping
    void getFoo();
"""
    }

    @Unroll
    void "writes simple data type response (#type)" () {
        def endpoint = createEndpoint (path: "/$type", method: HttpMethod.GET, responses: [
            '200': [new Response(contentType: 'text/plain', responseType: responseType)]
        ])

        when:
        writer.write (target, endpoint, endpoint.endpointResponses.first ())

        then:
        target.toString () == """\
    @CoreMapping
    ${type.capitalize ()} get${type.capitalize ()}();
"""

        where:
        type      | responseType
        'string'  | new StringDataType ()
        'integer' | new IntegerDataType ()
        'long'    | new LongDataType ()
        'float'   | new FloatDataType ()
        'double'  | new DoubleDataType ()
        'boolean' | new BooleanDataType ()
    }

    void "writes inline object data type response" () {
        def endpoint = createEndpoint (path: '/foo', method: HttpMethod.GET, responses: [
            '200': [
                new Response (contentType: 'application/json',
                    responseType: new ObjectDataType (
                        'InlineObjectResponse', '', [
                        foo1: new StringDataType (),
                        foo2: new StringDataType ()
                    ], null, false))
            ]
        ])

        when:
        writer.write (target, endpoint, endpoint.endpointResponses.first ())

        then:
        target.toString () == """\
    @CoreMapping
    InlineObjectResponse getFoo();
"""
    }

    void "writes method with Collection response type" () {
        def endpoint = createEndpoint (path: '/foo', method: HttpMethod.GET, responses: [
            '200': [
                new Response (contentType: 'application/json', responseType: collection)
            ]
        ])

        when:
        writer.write (target, endpoint, endpoint.endpointResponses.first ())

        then:
        target.toString () == """\
    @CoreMapping
    ${response} getFoo();
"""

        where:
        collection                                                                    | response
        new ArrayDataType (new StringDataType (), null, false)                        | 'String[]'
        new MappedCollectionDataType ('List', '', new StringDataType (), null, false) | 'List<String>'
        new MappedCollectionDataType ('Set', '', new StringDataType (), null, false)  | 'Set<String>'
    }

    void "writes parameter annotation" () {
        def endpoint = createEndpoint (path: '/foo', method: HttpMethod.GET, responses: [
            '204': [new EmptyResponse ()]
        ], parameters: [
            new ParameterBase () {
                String getName () {
                    "foo"
                }

                DataType getDataType () {
                    new StringDataType ()
                }

                @Override
                boolean isRequired () {
                    true
                }
            }
        ])

        when:
        writer.write (target, endpoint, endpoint.endpointResponses.first ())

        then:
        target.toString () == """\
    @CoreMapping
    void getFoo(@Parameter String foo);
"""
    }

    void "does not write parameter annotation if empty" () {
        def stubWriter = Stub (ParameterAnnotationWriter) {}

        writer.parameterAnnotationWriter = stubWriter
        def endpoint = createEndpoint (path: '/foo', method: HttpMethod.GET, responses: [
            '204': [new EmptyResponse ()]
        ], parameters: [Stub (Parameter) {
            getName () >> 'foo'
            getDataType () >> new StringDataType()
        }])

        when:
        writer.write (target, endpoint, endpoint.endpointResponses.first ())

        then:
        target.toString () == """\
    @CoreMapping
    void getFoo(String foo);
"""
    }

    void "writes method name from path with valid java identifiers" () {
        def endpoint = createEndpoint (path: '/f_o-ooo/b_a-rrr', method: HttpMethod.GET, responses: [
            '204': [new EmptyResponse ()]
        ])

        when:
        writer.write (target, endpoint, endpoint.endpointResponses.first ())

        then:
        target.toString () == """\
    @CoreMapping
    void getFOOooBARrr();
"""
    }

    void "writes method name from operation id with valid java identifiers" () {
        def endpoint = createEndpoint (path: '/foo', method: HttpMethod.GET, operationId: 'get-bar',
            responses: [
                '204': [new EmptyResponse ()]
            ])

        when:
        writer.write (target, endpoint, endpoint.endpointResponses.first ())

        then:
        target.toString () == """\
    @CoreMapping
    void getBar();
"""
    }

    void "writes method parameter with valid java identifiers" () {
        def endpoint = createEndpoint (path: '/foo', method: HttpMethod.GET, responses: [
            '204': [new EmptyResponse ()]
        ], parameters: [
            new QueryParameter(name: '_fo-o', required: true, dataType: new StringDataType())
        ])

        when:
        writer.write (target, endpoint, endpoint.endpointResponses.first ())

        then:
        target.toString () == """\
    @CoreMapping
    void getFoo(@Parameter String foO);
"""
    }

    void "writes method with void response type wrapped by result wrapper" () {
        def endpoint = createEndpoint (path: '/foo', method: HttpMethod.GET, responses: [
            '204': [new Response(responseType:
                new ResultDataType (
                    'ResultWrapper',
                    'http',
                    new NoneDataType ()
                        .wrappedInResult ()
                ))]
        ])

        when:
        writer.write (target, endpoint, endpoint.endpointResponses.first ())

        then:
        target.toString () == """\
    @CoreMapping
    ResultWrapper<Void> getFoo();
"""
    }

    void "writes method with 'Object' response when it has multiple result content types (200, default)" () {
        def endpoint = createEndpoint (path: '/foo', method: HttpMethod.GET, responses: [
            '200' : [
                new Response (contentType: 'application/json',
                    responseType: new StringDataType ())
            ],
            'default': [
                new Response (contentType: 'text/plain',
                    responseType: new StringDataType ())
            ]
        ])

        when:
        writer.write (target, endpoint, endpoint.endpointResponses.first ())

        then:
        target.toString () == """\
    @CoreMapping
    Object getFoo();
"""
    }

    void "writes method with '?' response when it has multiple result contents types & wrapper result type" () {
        def endpoint = createEndpoint (path: '/foo', method: HttpMethod.GET, responses: [
            '200' : [
                new Response (contentType: 'application/json',
                    responseType: new ResultDataType (
                        'ResultWrapper',
                        'http',
                        new StringDataType ()))
            ],
            'default': [
                new Response (contentType: 'text/plain',
                    responseType: new ResultDataType (
                        'ResultWrapper',
                        'http',
                        new StringDataType ()))
            ]
        ])

        when:
        writer.write (target, endpoint, endpoint.endpointResponses.first ())

        then:
        target.toString () == """\
    @CoreMapping
    ResultWrapper<?> getFoo();
"""
    }

}
