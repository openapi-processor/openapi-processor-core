/*
 * Copyright © 2019 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package com.github.hauner.openapi.core.writer.java

import io.openapiprocessor.core.converter.ApiOptions
import io.openapiprocessor.core.model.datatypes.AnnotationDataType
import io.openapiprocessor.core.model.datatypes.NoneDataType
import io.openapiprocessor.core.model.datatypes.ResultDataType
import io.openapiprocessor.core.model.parameters.Parameter
import io.openapiprocessor.core.model.parameters.ParameterBase
import io.openapiprocessor.core.model.Endpoint
import io.openapiprocessor.core.model.Response
import io.openapiprocessor.core.model.datatypes.ArrayDataType
import io.openapiprocessor.core.model.datatypes.BooleanDataType
import io.openapiprocessor.core.model.datatypes.DoubleDataType
import io.openapiprocessor.core.model.datatypes.FloatDataType
import io.openapiprocessor.core.model.datatypes.IntegerDataType
import io.openapiprocessor.core.model.datatypes.LongDataType
import io.openapiprocessor.core.model.datatypes.MappedCollectionDataType
import io.openapiprocessor.core.model.datatypes.ObjectDataType
import io.openapiprocessor.core.model.datatypes.StringDataType
import io.openapiprocessor.core.model.parameters.QueryParameter
import io.openapiprocessor.core.model.EmptyResponse
import com.github.hauner.openapi.core.test.TestMappingAnnotationWriter
import com.github.hauner.openapi.core.test.TestParameterAnnotationWriter
import io.openapiprocessor.core.model.HttpMethod
import io.openapiprocessor.core.writer.java.BeanValidationFactory
import io.openapiprocessor.core.writer.java.JavaDocWriter
import io.openapiprocessor.core.writer.java.MethodWriter
import io.openapiprocessor.core.writer.java.ParameterAnnotationWriter
import spock.lang.Specification
import spock.lang.Unroll

import static io.openapiprocessor.core.model.Builder.endpoint

class MethodWriterSpec extends Specification {
    def apiOptions = new ApiOptions()

    def writer = new MethodWriter (
        apiOptions,
        new TestMappingAnnotationWriter(),
        new TestParameterAnnotationWriter(),
        Stub (BeanValidationFactory),
        Stub (JavaDocWriter))
    def target = new StringWriter ()

    @Deprecated // use endpoint() builder
    private Endpoint createEndpoint (Map properties) {
        def ep = new Endpoint(
            properties.path as String ?: '',
            properties.method as HttpMethod ?: HttpMethod.GET,
            properties.operationId as String ?: null,
            properties.deprecated as boolean ?: false,
            properties.description as String ?: null
        )
        ep.parameters = properties.parameters ?: []
        ep.responses = properties.responses ?: [:]
        ep.initEndpointResponses ()
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
            '200': [new Response('text/plain', responseType)]
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
                new Response ('application/json',
                    new ObjectDataType (
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
                new Response ('application/json', collection)
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
            new ParameterBase (
                'foo', new StringDataType(null, false),
                true,
                false,
                null) {}
        ])

        when:
        writer.write (target, endpoint, endpoint.endpointResponses.first ())

        then:
        target.toString () == """\
    @CoreMapping
    void getFoo(@Parameter String foo);
"""
    }

    void "writes no parameter annotation if the annotation writer skips it" () {
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

    void "writes additional parameter annotation" () {
        def endpoint = endpoint('/foo') {
            parameters {
                add {
                    name ('foo')
                    type (new StringDataType())
                    annotation (new AnnotationDataType ('Foo', 'oap', '()'))
                }
            }
            responses ('204') {
                empty ()
            }
        }

        when:
        writer.write (target, endpoint, endpoint.endpointResponses.first ())

        then:
        target.toString () == """\
    @CoreMapping
    void getFoo(@Parameter @Foo() String foo);
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
            new QueryParameter(
                '_fo-o',
                new StringDataType(),
                true,
                false,
                null)
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
            '204': [new Response("",
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

    void "writes method with success response type when it has only empty error responses" () {
        def endpoint = createEndpoint (path: '/foo', method: HttpMethod.GET, responses: [
            '200' : [
                new Response ('application/json', new StringDataType ())
            ],
            '400': [
                new EmptyResponse ()
            ],
            '500': [
                new EmptyResponse ()
            ]
        ])

        when:
        writer.write (target, endpoint, endpoint.endpointResponses.first ())

        then:
        target.toString () == """\
    @CoreMapping
    String getFoo();
"""
    }

    void "writes method with 'Object' response when it has multiple result content types (200, default)" () {
        def endpoint = createEndpoint (path: '/foo', method: HttpMethod.GET, responses: [
            '200' : [
                new Response ('application/json', new StringDataType ())
            ],
            'default': [
                new Response ('text/plain', new StringDataType ())
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
                new Response ('application/json',
                    new ResultDataType (
                        'ResultWrapper',
                        'http',
                        new StringDataType ()))
            ],
            'default': [
                new Response ( 'text/plain',
                    new ResultDataType (
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
