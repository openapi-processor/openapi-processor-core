/*
 * Copyright 2020 the original authors
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

import io.openapiprocessor.core.converter.ApiConverter
import io.openapiprocessor.core.framework.Framework
import io.openapiprocessor.core.model.datatypes.LazyDataType
import io.openapiprocessor.core.converter.ApiOptions
import io.openapiprocessor.core.model.datatypes.ObjectDataType
import io.openapiprocessor.core.model.datatypes.PropertyDataType
import spock.lang.Specification

import static com.github.hauner.openapi.core.test.OpenApiParser.parse


class DataTypeConverterLoopSpec extends Specification {

    void "handles \$ref loops"() {
        def openApi = parse (
"""\
openapi: 3.0.2
info:
  title: test \$ref loop
  version: 1.0.0

paths:

  /self-reference:
    get:
      responses:
        '200':
          description: none
          content:
            application/json:
                schema:
                  \$ref: '#/components/schemas/Self'

components:
  schemas:

    Self:
      type: object
      properties:
        self:
          \$ref: '#/components/schemas/Self'
""")

        when:
        def api = new ApiConverter (new ApiOptions(), Stub (Framework))
            .convert (openApi)

        then:
        def itf = api.interfaces.first ()
        def ep = itf.endpoints.first ()
        def rp = ep.getFirstResponse ('200')
        def rt = rp.responseType as ObjectDataType
        def pt = rt.properties.self
        def sf = pt.dataType
        rt instanceof ObjectDataType
        pt instanceof PropertyDataType
        sf instanceof LazyDataType
        sf.name == 'Self'
        sf.packageName == 'io.openapiprocessor.generated.model'
        sf.imports == ['io.openapiprocessor.generated.model.Self'] as Set
        sf.referencedImports == ['io.openapiprocessor.generated.model.Self'] as Set
    }

}
