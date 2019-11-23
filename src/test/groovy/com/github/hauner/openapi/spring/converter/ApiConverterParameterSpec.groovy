/*
 * Copyright 2019 the original authors
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

package com.github.hauner.openapi.spring.converter

import spock.lang.Ignore
import spock.lang.Specification

import static com.github.hauner.openapi.spring.support.OpenApiParser.parse

class ApiConverterParameterSpec extends Specification {

    void "converts simple query parameter"() {
        def openApi = parse (
"""\
openapi: 3.0.2
info:
  title: test simple query parameter
  version: 1.0.0

paths:
  /endpoint:

    get:
      tags:
        - endpoint
      parameters:
        - name: foo
          description: query, required, string
          in: query
          required: true
          schema:
            type: string
      responses:
        '204':
          description: empty
""")

        when:
        def api = new ApiConverter ().convert (openApi)

        then:
        def itf = api.interfaces.first ()
        def ep = itf.endpoints.first ()
        def param = ep.parameters.first ()
        param.name == 'foo'
        param.required
        param.dataType.name == 'String'
        param.annotation == '@RequestParam'
        param.annotationWithPackage == 'org.springframework.web.bind.annotation.RequestParam'
    }

    void "converts simple path parameter"() {
        def openApi = parse (
"""\
openapi: 3.0.2
info:
  title: test simple path parameter
  version: 1.0.0

paths:
  /endpoint/{foo}:

    get:
      tags:
        - endpoint
      parameters:
        - name: foo
          description: path, required, string
          in: path
          required: true
          schema:
            type: string
      responses:
        '204':
          description: empty
""")

        when:
        def api = new ApiConverter ().convert (openApi)

        then:
        def itf = api.interfaces.first ()
        def ep = itf.endpoints.first ()
        def param = ep.parameters.first ()
        param.name == 'foo'
        param.required
        param.dataType.name == 'String'
        param.annotation == '@PathVariable'
        param.annotationWithPackage == 'org.springframework.web.bind.annotation.PathVariable'
    }

    void "converts simple header parameter"() {
        def openApi = parse (
"""\
openapi: 3.0.2
info:
  title: test simple header parameter
  version: 1.0.0

paths:
  /endpoint:

    get:
      tags:
        - endpoint
      parameters:
        - name: x-foo
          description: header, required, string
          in: header
          required: true
          schema:
            type: string
      responses:
        '204':
          description: empty
""")

        when:
        def api = new ApiConverter ().convert (openApi)

        then:
        def itf = api.interfaces.first ()
        def ep = itf.endpoints.first ()
        def param = ep.parameters.first ()
        param.name == 'x-foo'
        param.required
        param.dataType.name == 'String'
        param.annotation == '@RequestHeader'
        param.annotationWithPackage == 'org.springframework.web.bind.annotation.RequestHeader'
    }

    void "converts simple cookie parameter"() {
        def openApi = parse (
"""\
openapi: 3.0.2
info:
  title: test simple cookie parameter
  version: 1.0.0

paths:
  /endpoint:

    get:
      tags:
        - endpoint
      parameters:
        - name: foo
          description: cookie, required, string
          in: cookie
          required: true
          schema:
            type: string
      responses:
        '204':
          description: empty
""")

        when:
        def api = new ApiConverter ().convert (openApi)

        then:
        def itf = api.interfaces.first ()
        def ep = itf.endpoints.first ()
        def param = ep.parameters.first ()
        param.name == 'foo'
        param.required
        param.dataType.name == 'String'
        param.annotation == '@CookieValue'
        param.annotationWithPackage == 'org.springframework.web.bind.annotation.CookieValue'
    }

    @Ignore("the openapi parser ignores parameters with unknown types")
    void "throws on unknown parameter"() {
        def openApi = parse (
"""\
openapi: 3.0.2
info:
  title: test unknown parameter type
  version: 1.0.0

paths:
  /endpoint:

    get:
      tags:
        - endpoint
      parameters:
        - name: foo
          description: unknown, required, string
          in: unknown
          schema:
            type: string
      responses:
        '204':
          description: empty
""")

        when:
        new ApiConverter ().convert (openApi)

        then:
        def e = thrown (UnknownParameterTypeException)
        e.name == 'foo'
        e.type == 'unknown'
    }

}
