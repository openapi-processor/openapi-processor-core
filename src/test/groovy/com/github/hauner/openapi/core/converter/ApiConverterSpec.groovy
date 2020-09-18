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

package com.github.hauner.openapi.core.converter

import io.openapiprocessor.core.converter.ApiOptions
import io.openapiprocessor.core.converter.mapping.EndpointTypeMapping
import io.openapiprocessor.core.framework.Framework
import io.openapiprocessor.core.framework.FrameworkAnnotations
import io.openapiprocessor.core.writer.java.BeanValidationFactory
import io.openapiprocessor.core.writer.java.DefaultImportFilter
import io.openapiprocessor.core.writer.java.MappingAnnotationWriter
import io.openapiprocessor.core.writer.java.ParameterAnnotationWriter
import io.openapiprocessor.core.writer.java.SimpleWriter
import com.github.hauner.openapi.core.test.ModelAsserts
import io.openapiprocessor.core.writer.java.InterfaceWriter
import io.openapiprocessor.core.writer.java.MethodWriter
import spock.lang.Specification
import spock.lang.Unroll

import static com.github.hauner.openapi.core.test.OpenApiParser.parse

class ApiConverterSpec extends Specification implements ModelAsserts {

    void "groups endpoints into interfaces by first operation tag" () {
        def openApi = parse (
"""\
openapi: 3.0.2
info:
  title: API
  version: 1.0.0

paths:
  /a:
    get:
      tags:
        - ping
      responses:
        '204':
          description: none
  /b:
    get:
      tags:
        - pong
      responses:
        '204':
          description: none
  /c:
    get:
      tags:
        - ping
        - pong
      responses:
        '204':
          description: none
""")

        when:
        api = new ApiConverter (new ApiOptions(), Stub (Framework))
            .convert (openApi)

        then:
        assertInterfaces ('ping', 'pong')
        assertPingEndpoints ('/a', '/c')
        assertPongEndpoints ('/b')
    }


    @Unroll
    void "groups endpoints with method #method into interfaces" () {
        def openApi = parse (
"""\
openapi: 3.0.2
info:
  title: API
  version: 1.0.0

paths:
  /a:
    ${method}:
      tags:
        - ${method}
      responses:
        '204':
          description: no content
""")

        when:
        def opts = new ApiOptions()
        api = new ApiConverter (opts, Stub (Framework))
            .convert (openApi)

        def w = new InterfaceWriter (
            opts,
            Stub (SimpleWriter),
            new MethodWriter(
                new ApiOptions(),
                Stub (MappingAnnotationWriter),
                Stub (ParameterAnnotationWriter),
                Stub (BeanValidationFactory)),
            Stub (FrameworkAnnotations),
            new BeanValidationFactory(),
            new DefaultImportFilter())
        def writer = new StringWriter()
        w.write (writer, api.interfaces.get (0))

        then:
        assertInterfaces (method)
        assertEndpoints (method,'/a')

        where:
        method << ['get', 'put', 'post', 'delete', 'options', 'head', 'patch', 'trace']
    }

    void "sets interface package from processor options with 'api' sub package" () {
        def openApi = parse (
"""\
openapi: 3.0.2
info:
  title: API
  version: 1.0.0

paths:
  /foo:
    get:
      responses:
        '204':
          description: no content
""")

        def options = new ApiOptions(
            packageName: 'a.package.name'
        )

        when:
        def api = new ApiConverter (options, Stub (Framework))
            .convert (openApi)

        then:
        api.interfaces.first ().packageName == [options.packageName, 'api'].join ('.')
    }

    void "sets empty interface name when no interface name tag was provided" () {
        def openApi = parse (
"""\
openapi: 3.0.2
info:
  title: API
  version: 1.0.0

paths:
  /foo:
    get:
      responses:
        '204':
          description: no content
""")

        when:
        def api = new ApiConverter (new ApiOptions(), Stub (Framework))
            .convert (openApi)

        then:
        api.interfaces.first ().interfaceName == 'Api'
    }

    void "creates 'Excluded' interface when an endpoint should be skipped" () {
        def openApi = parse (
"""\
openapi: 3.0.2
info:
  title: API
  version: 1.0.0

paths:
  /foo:
    get:
      responses:
        '204':
          description: no content

  /bar:
    get:
      responses:
        '204':
          description: no content
""")

        def options = new ApiOptions(typeMappings: [
            new EndpointTypeMapping ('/foo', [], true)
        ])

        when:
        def api = new ApiConverter (options, Stub (Framework))
            .convert (openApi)

        then:
        def result = api.interfaces
        result.size () == 2
        result[0].interfaceName == 'Api'
        result[1].interfaceName == 'ExcludedApi'
    }

}
