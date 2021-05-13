/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package com.github.hauner.openapi.core.writer.java

import com.github.hauner.openapi.core.test.TestMappingAnnotationWriter
import com.github.hauner.openapi.core.test.TestParameterAnnotationWriter
import io.openapiprocessor.core.converter.ApiOptions
import io.openapiprocessor.core.model.HttpMethod
import io.openapiprocessor.core.model.datatypes.*
import io.openapiprocessor.core.model.parameters.Parameter
import io.openapiprocessor.core.model.parameters.ParameterBase
import io.openapiprocessor.core.support.datatypes.ObjectDataType
import io.openapiprocessor.core.writer.java.BeanValidationFactory
import io.openapiprocessor.core.writer.java.JavaDocWriter
import io.openapiprocessor.core.writer.java.MethodWriter
import io.openapiprocessor.core.writer.java.ParameterAnnotationWriter
import spock.lang.Specification
import spock.lang.Unroll

import static io.openapiprocessor.core.builder.api.EndpointBuilderKt.endpoint

class MethodWriterSpec extends Specification {
    def apiOptions = new ApiOptions()

    def writer = new MethodWriter (
        apiOptions,
        new TestMappingAnnotationWriter(),
        new TestParameterAnnotationWriter(),
        Stub (BeanValidationFactory),
        Stub (JavaDocWriter))
    def target = new StringWriter ()

    void "writes mapping annotation" () {
        def endpoint = endpoint('/foo', HttpMethod.GET) { e ->
            e.responses { r ->
                r.status ('204') {it.empty () }
            }
        }

        when:
        writer.write (target, endpoint, endpoint.endpointResponses.first ())

        then:
        target.toString () == """\
    @CoreMapping
    void getFoo();
"""
    }

    void "writes @Deprecated annotation" () {
        def endpoint = endpoint ('/foo', HttpMethod.GET) {e ->
            e.deprecated ()
            e.responses {r ->
                r.status ('204') {it.empty () }
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
        def endpoint = endpoint ("/$type", HttpMethod.GET) {e ->
            e.responses { rs ->
                rs.status ('200') { r ->
                    r.response ('text/plain', responseType)
                }
            }
        }

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
        def endpoint = endpoint ("/foo", HttpMethod.GET) {e ->
            e.responses { rs ->
                rs.status ('200') { r ->
                    r.response ('application/json',
                        new ObjectDataType (
                            'InlineObjectResponse', '', [
                            foo1: new StringDataType (),
                            foo2: new StringDataType ()
                        ], null, false, null)) {}
                }
            }
        }

        when:
        writer.write (target, endpoint, endpoint.endpointResponses.first ())

        then:
        target.toString () == """\
    @CoreMapping
    InlineObjectResponse getFoo();
"""
    }

    void "writes method with Collection response type" () {
        def endpoint = endpoint ("/foo", HttpMethod.GET) {e ->
            e.responses { rs ->
                rs.status ('200') { r ->
                    r.response ('application/json', collection)
                }
            }
        }

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
        def endpoint = endpoint ("/foo", HttpMethod.GET) {e ->
            e.responses { rs ->
                rs.status ('204') { r -> r.empty () }
            }
            e.parameters { ps ->
                ps.any (new ParameterBase (
                    'foo', new StringDataType (null, false, null),
                    true,
                    false,
                    null) {})
            }
        }

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

        def endpoint = endpoint ("/foo", HttpMethod.GET) {e ->
            e.responses { rs ->
                rs.status ('204') { r -> r.empty () }
            }
            e.parameters { ps -> ps.any (
                Stub (Parameter) {
                    getName () >> 'foo'
                    getDataType () >> new StringDataType ()
                })
            }
        }

        when:
        writer.write (target, endpoint, endpoint.endpointResponses.first ())

        then:
        target.toString () == """\
    @CoreMapping
    void getFoo(String foo);
"""
    }

    void "writes additional parameter annotation" () {
        def endpoint = endpoint ("/foo", HttpMethod.GET) {e ->
            e.responses { rs ->
                rs.status ('204') { r -> r.empty () }
            }
            e.parameters { ps ->
                ps.add ('foo', new StringDataType()) { a ->
                    a.annotation = new AnnotationDataType ('Foo', 'oap', '()')
                }
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
        def endpoint = endpoint ('/f_o-ooo/b_a-rrr', HttpMethod.GET) {e ->
            e.responses {r ->
                r.status ('204') {it.empty () }
            }
        }

        when:
        writer.write (target, endpoint, endpoint.endpointResponses.first ())

        then:
        target.toString () == """\
    @CoreMapping
    void getFOOooBARrr();
"""
    }

    void "writes method name from operation id with valid java identifiers" () {
        def endpoint = endpoint ('/foo', HttpMethod.GET) {e ->
            e.operationId = 'get-bar'
            e.responses {r ->
                r.status ('204') {it.empty () }
            }
        }

        when:
        writer.write (target, endpoint, endpoint.endpointResponses.first ())

        then:
        target.toString () == """\
    @CoreMapping
    void getBar();
"""
    }

    void "writes method parameter with valid java identifiers" () {
        def endpoint = endpoint ("/foo", HttpMethod.GET) {e ->
            e.responses { rs ->
                rs.status ('204') { r -> r.empty () }
            }
            e.parameters { ps ->
                ps.query ('_fo-o', new StringDataType()) { q ->
                    q.required = true
                    q.deprecated = false
                }
            }
        }

        when:
        writer.write (target, endpoint, endpoint.endpointResponses.first ())

        then:
        target.toString () == """\
    @CoreMapping
    void getFoo(@Parameter String foO);
"""
    }

    void "writes method with void response type wrapped by result wrapper" () {
        def endpoint = endpoint ("/foo", HttpMethod.GET) {e ->
            e.responses { rs ->
                rs.status ('204') { r ->
                    r.response ('', new ResultDataType (
                        'ResultWrapper',
                        'http',
                        new NoneDataType ()
                            .wrappedInResult ()
                    ))
                }
            }
        }

        when:
        writer.write (target, endpoint, endpoint.endpointResponses.first ())

        then:
        target.toString () == """\
    @CoreMapping
    ResultWrapper<Void> getFoo();
"""
    }

    void "writes method with success response type when it has only empty error responses" () {
        def endpoint = endpoint ("/foo", HttpMethod.GET) {e ->
            e.responses { rs ->
                rs.status ('200') { r ->
                    r.response ('application/json', new StringDataType ())
                }
                rs.status ('400') { r ->
                    r.empty ()
                }
                rs.status ('500') { r ->
                    r.empty ()
                }
            }
        }

        when:
        writer.write (target, endpoint, endpoint.endpointResponses.first ())

        then:
        target.toString () == """\
    @CoreMapping
    String getFoo();
"""
    }

    void "writes method with 'Object' response when it has multiple result content types (200, default)" () {
        def endpoint = endpoint ("/foo", HttpMethod.GET) {e ->
            e.responses { rs ->
                rs.status ('200') { r ->
                    r.response ('application/json', new StringDataType ())
                }
                rs.status ('default') { r ->
                    r.response ('text/plain', new StringDataType ())
                }
            }
        }

        when:
        writer.write (target, endpoint, endpoint.endpointResponses.first ())

        then:
        target.toString () == """\
    @CoreMapping
    Object getFoo();
"""
    }

    void "writes method with '?' response when it has multiple result contents types & wrapper result type" () {
        def endpoint = endpoint ("/foo", HttpMethod.GET) {e ->
            e.responses { rs ->
                rs.status ('200') { r ->
                    r.response ('application/json', new ResultDataType (
                        'ResultWrapper',
                        'http',
                        new StringDataType ()))
                }
                rs.status ('default') { r ->
                    r.response ('text/plain', new ResultDataType (
                        'ResultWrapper',
                        'http',
                        new StringDataType ()))
                }
            }
        }

        when:
        writer.write (target, endpoint, endpoint.endpointResponses.first ())

        then:
        target.toString () == """\
    @CoreMapping
    ResultWrapper<?> getFoo();
"""
    }

}
