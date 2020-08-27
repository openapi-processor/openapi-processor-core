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

import io.openapiprocessor.core.converter.ApiOptions
import com.github.hauner.openapi.core.framework.FrameworkAnnotation
import com.github.hauner.openapi.core.framework.FrameworkAnnotations
import com.github.hauner.openapi.core.model.Endpoint
import io.openapiprocessor.core.model.EndpointResponse
import com.github.hauner.openapi.core.model.Interface
import io.openapiprocessor.core.model.RequestBody
import io.openapiprocessor.core.model.Response
import io.openapiprocessor.core.model.datatypes.MappedDataType
import io.openapiprocessor.core.model.datatypes.NoneDataType
import io.openapiprocessor.core.model.datatypes.ObjectDataType
import io.openapiprocessor.core.model.datatypes.ResultDataType
import io.openapiprocessor.core.model.datatypes.StringDataType
import io.openapiprocessor.core.model.parameters.ParameterBase
import io.openapiprocessor.core.model.parameters.QueryParameter
import io.openapiprocessor.core.model.test.EmptyResponse
import io.openapiprocessor.core.model.HttpMethod
import io.openapiprocessor.core.writer.java.NullImportFilter
import spock.lang.Specification

import java.util.stream.Collectors

import static com.github.hauner.openapi.core.test.AssertHelper.extractImports
import static io.openapiprocessor.core.model.Builder.intrface

class InterfaceWriterSpec extends Specification {
    def headerWriter = Mock SimpleWriter
    def methodWriter = Stub MethodWriter
    def annotations = Stub (FrameworkAnnotations)
    def apiOptions = new ApiOptions()

    def writer = new InterfaceWriter(
        headerWriter: headerWriter,
        methodWriter: methodWriter,
        annotations: annotations,
        apiOptions: apiOptions)
    def target = new StringWriter ()

    void "writes 'generated' comment" () {
        def apiItf = new Interface ()

        when:
        writer.write (target, apiItf)

        then:
        1 * headerWriter.write (target)
    }

    void "writes 'package'" () {
        def pkg = 'com.github.hauner.openapi'
        def apiItf = new Interface (pkg: pkg)

        when:
        writer.write (target, apiItf)

        then:
        target.toString ().contains (
"""\
package $pkg;

""")
    }

    void "writes mapping import" () {
        annotations.getAnnotation (_) >> new FrameworkAnnotation(name: 'Mapping', pkg: 'annotation')

        def apiItf = new Interface (name: 'name', endpoints: [
            new Endpoint(path: '/foo', method: HttpMethod.GET, responses: [
                '200': [new EmptyResponse()]])
        ])

        when:
        writer.write (target, apiItf)

        then:
        def result = extractImports (target.toString ())
        result.contains("""\
import annotation.Mapping;
""")
    }

    void "writes multiple mapping imports" () {
        annotations.getAnnotation (_) >>> [
            new FrameworkAnnotation(name: 'MappingA', pkg: 'annotation'),
            new FrameworkAnnotation(name: 'MappingB', pkg: 'annotation'),
            new FrameworkAnnotation(name: 'MappingC', pkg: 'annotation')
        ]

        def apiItf = new Interface (name: 'name', endpoints: [
            new Endpoint(path: 'path', method: HttpMethod.GET, responses: ['200': [new EmptyResponse()]]),
            new Endpoint(path: 'path', method: HttpMethod.PUT, responses: ['200': [new EmptyResponse()]]),
            new Endpoint(path: 'path', method: HttpMethod.POST, responses: ['200': [new EmptyResponse()]])
        ])

        when:
        writer.write (target, apiItf)

        then:
        def result = extractImports (target.toString ())
        result.contains("""\
import annotation.MappingA;
""")
        result.contains("""\
import annotation.MappingB;
""")
        result.contains("""\
import annotation.MappingC;
""")
    }

    void "writes result wrapper data type import" () {
        def apiItf = new Interface (name: 'name', endpoints: [
            new Endpoint(path: 'path', method: HttpMethod.GET, responses: [
                '200': [
                    new Response ("",
                        new ResultDataType (
                            'ResultWrapper',
                            'http',
                            new NoneDataType ()
                        ))
                ]]).initEndpointResponses ()
        ])

        when:
        writer.write (target, apiItf)

        then:
        def result = extractImports (target.toString ())
        result.contains("""\
http.ResultWrapper;
""")
    }

    void "writes parameter annotation import" () {
        annotations.getAnnotation (_) >> new FrameworkAnnotation(name: 'Parameter', pkg: 'annotation')

        def apiItf = new Interface (name: 'name', endpoints: [
            new Endpoint(path: 'path', method: HttpMethod.GET, responses: ['200': [new EmptyResponse()]],
                parameters: [
                    new QueryParameter('any', new StringDataType(), false, false)
                ])
        ])

        when:
        writer.write (target, apiItf)

        then:
        def result = extractImports (target.toString ())
        result.contains("""\
import annotation.Parameter;
""")
    }

    void "does not write parameter annotation import of a parameter that does not want the annotation" () {
        def endpoint = new Endpoint (path: '/foo', method: HttpMethod.GET, responses: [
            '200': [new Response ('application/json', new NoneDataType())]
        ], parameters: [
            new ParameterBase ('foo',
                new StringDataType(null, false),
                false, false) {

                @Override
                boolean getWithAnnotation () {
                    return false
                }
            }
        ])

        def apiItf = new Interface (name: 'name', endpoints: [endpoint])

        when:
        writer.write (target, apiItf)

        then:
        def result = extractImports (target.toString ())
        ! result.contains("""\
import annotation.Parameter;
""")
    }

    void "writes import of request parameter data type" () {
        def endpoint = new Endpoint (path: '/foo', method: HttpMethod.GET, responses: [
            '200': [new Response ( 'application/json', new NoneDataType())]
        ], parameters: [
            new QueryParameter('foo', new ObjectDataType (
                'Foo', 'model', [
                    foo1: new StringDataType (),
                    foo2: new StringDataType ()
                ], null, false
            ), false, false)
        ])

        def apiItf = new Interface (name: 'name', endpoints: [endpoint])

        when:
        writer.write (target, apiItf)

        then:
        def result = extractImports (target.toString ())
        result.contains("""\
import model.Foo;
""")
    }

    void "writes request body annotation import" () {
        annotations.getAnnotation (_) >> new FrameworkAnnotation(name: 'Body', pkg: 'annotation')

        def apiItf = new Interface (name: 'name', endpoints: [
            new Endpoint(path: '/foo', method: HttpMethod.GET, responses: [
                '200': [new EmptyResponse()]
            ], requestBodies: [
                new RequestBody ('body', 'plain/text', new StringDataType (),
                    true, false
                )
            ])
        ])

        when:
        writer.write (target, apiItf)

        then:
        def result = extractImports (target.toString ())
        result.contains("""\
import annotation.Body;
""")
    }

    void "writes import of request body data type" () {
        def endpoint = new Endpoint (path: '/foo', method: HttpMethod.GET, responses: [
            '200': [new EmptyResponse ()]
        ], requestBodies: [
            new RequestBody ('body', 'plain/text',
                new MappedDataType (
                    'Bar', 'com.github.hauner.openapi', [],
                    null, false),
                true,
                false
            )
        ])

        def apiItf = new Interface (name: 'name', endpoints: [endpoint])

        when:
        writer.write (target, apiItf)

        then:
        def result = extractImports (target.toString ())
        result.contains("""\
import com.github.hauner.openapi.Bar;
""")
    }

    void "writes model import"() {
        def pkg = 'model.package'
        def type = 'Model'

        def apiItf = new Interface (name: 'name', endpoints: [
            new Endpoint(path: 'path', method: HttpMethod.GET, responses: [
                '200': [
                    new Response ('application/json',
                        new ObjectDataType (type, pkg, [:], null, false))
                ]
            ]).initEndpointResponses ()
        ])

        when:
        writer.write (target, apiItf)

        then:
        def result = extractImports (target.toString ())
        result.contains("""\
import ${pkg}.${type};
""")
    }

    void "writes multiple response model import"() {
        def pkg = 'model.package'
        def type = 'Model'

        def pkg2 = 'model.package2'
        def type2 = 'Model2'

        def apiItf = new Interface (name: 'name', endpoints: [
            new Endpoint (path: 'path', method: HttpMethod.GET, responses: [
                '200': [
                    new Response ('application/json',
                        new ObjectDataType (type, pkg, [:], null, false)),
                    new Response ('text/plain',
                        new ObjectDataType (type2, pkg2, [:], null, false))
                ]
            ]).initEndpointResponses ()
        ])

        when:
        writer.write (target, apiItf)

        then:
        def result = extractImports (target.toString ())
        result.contains("""\
import ${pkg}.${type};
""")
        result.contains("""\
import ${pkg2}.${type2};
""")
    }

    void "writes @Deprecated import" () {
        writer.importFilter = new NullImportFilter()

        def apiItf = intrface ('name', {
            endpoint ('/foo', {
                get ()
                deprecated ()

                responses ('204') {
                    empty ()
                }
            })
        })

        when:
        writer.write (target, apiItf)

        then:
        def result = extractImports (target.toString ())
        result.contains("""\
import java.lang.Deprecated;
""")
    }

    void "sorts imports as strings"() {
        annotations.getAnnotation (_) >>> [
            new FrameworkAnnotation(name: 'MappingC', pkg: 'annotation'),
            new FrameworkAnnotation(name: 'MappingB', pkg: 'annotation'),
            new FrameworkAnnotation(name: 'MappingA', pkg: 'annotation')
        ]

        def apiItf = new Interface (name: 'name', endpoints: [
            new Endpoint(path: 'path', method: HttpMethod.GET, responses: ['200': [new EmptyResponse()]]),
            new Endpoint(path: 'path', method: HttpMethod.PUT, responses: ['200': [new EmptyResponse()]]),
            new Endpoint(path: 'path', method: HttpMethod.POST, responses: ['200': [new EmptyResponse()]])
        ])

        when:
        writer.write (target, apiItf)

        then:
        def result = extractImports (target.toString ())
        result.contains("""\
import annotation.MappingA;
import annotation.MappingB;
import annotation.MappingC;
""")
    }

    void "filters unnecessary 'java.lang' imports"() {
        def apiItf = new Interface (name: 'name', endpoints: [
            new Endpoint(path: 'path', method: HttpMethod.GET, responses: [
                '200': [new Response('plain/text', new StringDataType())]
            ])
        ])

        when:
        writer.write (target, apiItf)

        then:
        def result = extractImports (target.toString ())
        !result.contains("""\
import java.lang.String;
""")
    }

    void "writes 'interface' block" () {
        def apiItf = new Interface (name: 'name', endpoints: [])

        when:
        writer.write (target, apiItf)

        then:
        def result = extractInterfaceBlock(target.toString ())
        result == """\
public interface NameApi {
}
"""
    }

    void "writes methods" () {
        def endpoints = [
            new Endpoint(path: 'path1', method: HttpMethod.GET, responses: ['200': [new EmptyResponse()]])
                .initEndpointResponses (),
            new Endpoint(path: 'path2', method: HttpMethod.GET, responses: ['200': [new EmptyResponse()]])
                .initEndpointResponses ()
        ]

        writer.methodWriter.write (_ as Writer, _ as Endpoint, _ as EndpointResponse) >> {
            Writer target = it.get (0)
            Endpoint e = it.get (1)
            target.write ("// ${e.path}\n")
        }

        def apiItf = new Interface (name: 'name', endpoints: endpoints)

        when:
        writer.write (target, apiItf)

        then:
        def result = extractInterfaceBody(target.toString ())
        result == """\

// path1

// path2

"""
    }

    String extractInterfaceBlock (String source) {
        source.readLines ().stream ()
            .filter {it ==~ /public interface (.+?) \{/ || it ==~ /}/}
            .collect (Collectors.toList ())
            .join ('\n') + '\n'
    }

    String extractInterfaceBody (String source) {
        source
            .replaceFirst (/(?s)(.*?)interface (.+?) \{\n/, '')
            .replaceFirst (/(?s)}\n/, '')
    }

}
