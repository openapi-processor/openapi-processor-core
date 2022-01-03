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

import io.openapiprocessor.core.converter.ApiConverter
import io.openapiprocessor.core.converter.DataTypeConverter
import io.openapiprocessor.core.converter.SchemaInfo
import io.openapiprocessor.core.converter.mapping.MappingFinder
import io.openapiprocessor.core.converter.wrapper.NullDataTypeWrapper
import io.openapiprocessor.core.framework.Framework
import io.openapiprocessor.core.model.Api
import io.openapiprocessor.core.model.DataTypes
import io.openapiprocessor.core.converter.ApiOptions
import io.openapiprocessor.core.model.HttpMethod
import io.openapiprocessor.core.model.datatypes.ObjectDataType
import com.github.hauner.openapi.core.test.TestSchema
import io.openapiprocessor.core.converter.mapping.UnknownDataTypeException
import io.openapiprocessor.core.parser.NamedSchema
import io.openapiprocessor.core.parser.RefResolver
import io.openapiprocessor.core.parser.Schema
import org.jetbrains.annotations.NotNull
import spock.lang.Specification
import spock.lang.Unroll

import static com.github.hauner.openapi.core.test.OpenApiParser.parse

class DataTypeConverterSpec extends Specification {
    def converter = new DataTypeConverter(
        new ApiOptions(), new MappingFinder(), Stub(NullDataTypeWrapper))

    @Unroll
    void "converts schema(#type, #format) to #javaType" () {
        def schema = new TestSchema (type: type, format: format)

        when:
        def datatype = converter.convert (
            new SchemaInfo (
                new SchemaInfo.Endpoint("", HttpMethod.GET),
                javaType, "", schema, Stub(RefResolver)),
            new DataTypes())

        then:
        datatype.name == javaType

        where:
        type      | format      | javaType
        'string'  | null        | 'String'
        'string'  | 'date'      | 'LocalDate'
        'string'  | 'date-time' | 'OffsetDateTime'
        'integer' | null        | 'Integer'
        'integer' | 'int32'     | 'Integer'
        'integer' | 'int64'     | 'Long'
        'number'  | null        | 'Float'
        'number'  | 'float'     | 'Float'
        'number'  | 'double'    | 'Double'
        'boolean' | null        | 'Boolean'
    }

    @Unroll
    void "converts schema(#type, #format, #dflt) to javaType with default value" () {
        def schema = new TestSchema (type: type, format: format, defaultValue: dflt)

        when:
        def datatype = converter.convert (
            new SchemaInfo (
                new SchemaInfo.Endpoint("", HttpMethod.GET),
                javaType, "", schema, Stub(RefResolver)),
            new DataTypes())

        then:
        datatype.name == javaType
        datatype.constraints.default == dflt

        where:
        type      | format   | dflt  | javaType
        'string'  | null     | 'foo' | 'String'
        'integer' | null     | 101   | 'Integer'
        'integer' | 'int32'  | 102   | 'Integer'
        'integer' | 'int64'  | 103   | 'Long'
        'number'  | null     | 10.1  | 'Float'
        'number'  | 'float'  | 10.2  | 'Float'
        'number'  | 'double' | 10.3  | 'Double'
        'boolean' | null     | false | 'Boolean'
    }

    void "throws when hitting an unknown data type" () {
        def schema = new TestSchema (type: type, format: format)

        when:
        converter.convert (new SchemaInfo (
            new SchemaInfo.Endpoint("", HttpMethod.GET),
            "", "", schema, Stub (RefResolver)),
            new DataTypes())

        then:
        def e = thrown(UnknownDataTypeException)
        e.type == type
        e.format == format

        where:
        type | format
        'x'  | null
        'y'  | 'none'
    }

    void "converts object schema with ref" () {
        def dt = new DataTypes()

        Schema barSchema = new TestSchema (type: 'object', properties: [
            val: new TestSchema (type: 'string')
        ])
        Schema fooSchema = new TestSchema (type: 'object', properties: [
            bar: new TestSchema (type: 'object', ref: "#/components/schemas/Bar")
        ])

        def resolver = new RefResolver () {

            @Override
            NamedSchema resolve (@NotNull Schema ref) {
                return new NamedSchema("Bar", barSchema)
            }
        }

        when:
        converter.convert (
            new SchemaInfo (new SchemaInfo.Endpoint ("", HttpMethod.GET),
                'Bar', "", barSchema, Stub(RefResolver)),
            dt)
        converter.convert (
            new SchemaInfo (new SchemaInfo.Endpoint ("", HttpMethod.GET),
                'Foo', "", fooSchema, resolver),
            dt)

        then:
        assert dt.dataTypes.size () == 2
        def bar = dt.find ('Bar') as ObjectDataType
        bar.properties['val'].name == 'String'
        def foo = dt.find ('Foo') as ObjectDataType
        foo.properties['bar'].dataType == bar
    }

    void "converts simple array schema to Array[]" () {
        def openApi = parse ("""\
openapi: 3.0.2
info:
  title: API
  version: 1.0.0

paths:
  /array-string:
    get:
      responses:
        '200':
          content:
            application/vnd.array:
              schema:
                type: array
                items:
                  type: string
          description: none              
""")
        when:
        def options = new ApiOptions(packageName: 'pkg')

        Api api = new ApiConverter (options, Stub (Framework))
            .convert (openApi)

        then:
        def itf = api.interfaces.first ()
        def ep = itf.endpoints.first ()
        def rsp = ep.getFirstResponse ('200')
        rsp.responseType.name == 'String[]'
    }

    void "creates model for inline response object with name {path}{method}Response{response code}"() {
        def openApi = parse ("""\
openapi: 3.0.2
info:
  title: API
  version: 1.0.0

paths:
  /inline:
    get:
      responses:
        '200':
          description: none
          content:
            application/json:
              schema:
                type: object
                properties:
                  isbn:
                    type: string
                  title:
                    type: string                
""")
        when:
        def options = new ApiOptions(packageName: 'pkg')

        Api api = new ApiConverter (options, Stub (Framework))
            .convert (openApi)

        then:
        def itf = api.interfaces.first ()
        def ep = itf.endpoints.first ()
        def rsp = ep.getFirstResponse ('200')
        def props = (rsp.responseType as ObjectDataType).properties
        rsp.responseType.name == 'InlineGetResponse200'
        rsp.responseType.packageName == "${options.packageName}.model"
        props.size () == 2
        props.get ('isbn').name == 'String'
        props.get ('title').name == 'String'

        and:
        api.dataTypes.dataTypes.size () == 1
        api.dataTypes.find ('InlineGetResponse200') is rsp.responseType
    }

    void "creates model for component schema object" () {
        def openApi = parse ("""\
openapi: 3.0.2
info:
  title: component schema object
  version: 1.0.0

paths:
  /book:
    get:
      responses:
        '200':
          description: none
          content:
            application/json:
                schema:
                  \$ref: '#/components/schemas/Book'

components:
  schemas:
    Book:
      type: object
      properties:
        isbn:
          type: string
        title:
          type: string
""")
        when:
        def options = new ApiOptions(packageName: 'pkg')

        Api api = new ApiConverter (options, Stub (Framework))
            .convert (openApi)

        then:
        api.dataTypes.dataTypes.size () == 1

        and:
        def dataTypes = api.dataTypes
        def book = dataTypes.find ('Book') as ObjectDataType
        assert book.name == 'Book'
        assert book.packageName == "${options.packageName}.model"
        assert book.properties.size () == 2
        def isbn = book.properties.get('isbn')
        assert isbn.name == 'String'
        def title = book.properties.get('title')
        assert title.name == 'String'
    }

    void "skips named simple data types from #/components/schemas" () {
        def openApi = parse ("""\
openapi: 3.0.2
info:
  title: component simple schemas 
  version: 1.0.0

paths:
  /book:
    get:
      responses:
        '200':
          description: none
          content:
            application/json:
                schema:
                  \$ref: '#/components/schemas/Book'

components:
  schemas:
    Isbn:
      type: string
     
    Title:
      type: string

    Book:
      type: object
      properties:
        isbn:
          \$ref: '#/components/schemas/Isbn'
        title:
          \$ref: '#/components/schemas/Title'
          
""")
        when:
        def options = new ApiOptions(packageName: 'pkg')

        Api api = new ApiConverter (options, Stub (Framework))
            .convert (openApi)

        then:
        api.dataTypes.dataTypes.size () == 1

        and:
        def dataTypes = api.dataTypes
        assert dataTypes.find ('Book')
    }

    void "skips named array data types from #/components/schemas" () {
        def openApi = parse ("""\
openapi: 3.0.2
info:
  title: component array schemas 
  version: 1.0.0

paths:
  /book:
    get:
      responses:
        '200':
          description: none
          content:
            application/json:
                schema:
                  \$ref: '#/components/schemas/Book'

components:

  schemas:
    Authors:
      type: array
      items:
        \$ref: '#/components/schemas/Author'
        
    Author:      
      type: object
      properties:
        name:
          type: string
     
    Book:
      type: object
      properties:
        authors:
          \$ref: '#/components/schemas/Authors'
""")

        when:
        def options = new ApiOptions(packageName: 'pkg')

        Api api = new ApiConverter (options, Stub (Framework))
            .convert (openApi)

        then:
        api.dataTypes.dataTypes.size () == 2

        and:
        def dataTypes = api.dataTypes
        def book = dataTypes.find ('Book')
        assert book != null
        def author = dataTypes.find ('Author')
        assert author != null
    }


    void "preserves order of object properties" () {
        def openApi = parse ("""\
openapi: 3.0.2
info:
  title: API
  version: 1.0.0

paths:
  /endpoint:
    get:
      responses:
        '200':
          description: empty
          content:
            application/json:
              schema:
                type: object
                properties:
                  b:
                    type: string
                  a:
                    type: string
                  c:
                    type: string
""")

        when:
        def options = new ApiOptions(packageName: 'pkg')

        Api api = new ApiConverter (options, Stub (Framework))
            .convert (openApi)

        then:
        def itf = api.interfaces.first ()
        def ep = itf.endpoints.first ()
        def rsp = ep.getFirstResponse ('200')
        def rt = rsp.responseType as ObjectDataType
        def keys = rt.properties.keySet ()

        keys[0] == 'b'
        keys[1] == 'a'
        keys[2] == 'c'
    }

}
