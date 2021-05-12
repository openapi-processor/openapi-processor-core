/*
 * Copyright © 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.openapiprocessor.core.model.DataTypes
import io.openapiprocessor.core.model.HttpMethod
import io.openapiprocessor.core.model.datatypes.ObjectDataType
import io.openapiprocessor.core.model.datatypes.StringEnumDataType
import io.openapiprocessor.core.support.getBodySchemaInfo
import io.openapiprocessor.core.support.parse

class DataTypeConverterSuffixSpec: StringSpec({
    val dataTypes = DataTypes()

    "adds suffix to model data type name" {
        val options = ApiOptions()
        options.modelNameSuffix = "Suffix"

        val openApi = parse("""
           openapi: 3.0.2
           info:
             title: API
             version: 1.0.0
           
           paths:
             /foo:
               post:
                 requestBody:
                   content:
                     application/json:
                       schema:
                         ${'$'}ref: '#/components/schemas/Foo'
                 responses:
                   '204':
                     description: empty
           
           components:
             schemas:
           
               Foo:
                 description: a Foo
                 type: object
                 properties:
                   foo:
                     type: string
                 
        """.trimIndent())

        val schemaInfo = openApi.getBodySchemaInfo("Foo",
            "/foo", HttpMethod.POST, "application/json")

        // when:
        val converter = DataTypeConverter(options)
        val datatype = converter.convert(schemaInfo, dataTypes)

        // then:
        dataTypes.find("Foo") shouldBeSameInstanceAs datatype

        datatype.shouldBeInstanceOf<ObjectDataType>()
        datatype.getName().shouldBe("FooSuffix")
    }

    "ignores suffix if model data type name already ends with the suffix" {
        val options = ApiOptions()
        options.modelNameSuffix = "Suffix"

        val openApi = parse("""
           openapi: 3.0.2
           info:
             title: API
             version: 1.0.0
           
           paths:
             /foo:
               post:
                 requestBody:
                   content:
                     application/json:
                       schema:
                         ${'$'}ref: '#/components/schemas/FooWithSuffix'
                 responses:
                   '204':
                     description: empty
           
           components:
             schemas:
           
               FooWithSuffix:
                 description: a Foo
                 type: object
                 properties:
                   foo:
                     type: string
                 
        """.trimIndent())

        val schemaInfo = openApi.getBodySchemaInfo("FooWithSuffix",
            "/foo", HttpMethod.POST, "application/json")

        // when:
        val converter = DataTypeConverter(options)
        val datatype = converter.convert(schemaInfo, dataTypes)

        // then:
        dataTypes.find("FooWithSuffix") shouldBeSameInstanceAs datatype

        datatype.shouldBeInstanceOf<ObjectDataType>()
        datatype.getName().shouldBe("FooWithSuffix")
    }

    "adds suffix to model enum data type name" {
        val options = ApiOptions()
        options.modelNameSuffix = "Suffix"

        val openApi = parse("""
           openapi: 3.0.2
           info:
             title: API
             version: 1.0.0
           
           paths:
             /foo:
               post:
                 requestBody:
                   content:
                     application/json:
                       schema:
                         ${'$'}ref: '#/components/schemas/Bar'
                 responses:
                   '204':
                     description: empty
           
           components:
             schemas:
           
                Bar:
                  type: string
                  enum:
                    - bar-1
                    - bar-2
                 
        """.trimIndent())

        val schemaInfo = openApi.getBodySchemaInfo("Bar",
            "/foo", HttpMethod.POST, "application/json")

        // when:
        val converter = DataTypeConverter(options)
        val datatype = converter.convert(schemaInfo, dataTypes)

        // then:
        dataTypes.find("Bar") shouldBeSameInstanceAs datatype

        datatype.shouldBeInstanceOf<StringEnumDataType>()
        datatype.getName().shouldBe("Bar")
        datatype.getTypeName().shouldBe("BarSuffix")
    }

    "ignores suffix if model enum data type name already ends with the suffix" {
         val options = ApiOptions()
         options.modelNameSuffix = "Suffix"

         val openApi = parse("""
            openapi: 3.0.2
            info:
              title: API
              version: 1.0.0
            
            paths:
              /foo:
                post:
                  requestBody:
                    content:
                      application/json:
                        schema:
                          ${'$'}ref: '#/components/schemas/BarWithSuffix'
                  responses:
                    '204':
                      description: empty
            
            components:
              schemas:
            
                 BarWithSuffix:
                   type: string
                   enum:
                     - bar-1
                     - bar-2
                  
         """.trimIndent())

         val schemaInfo = openApi.getBodySchemaInfo("BarWithSuffix",
             "/foo", HttpMethod.POST, "application/json")

         // when:
         val converter = DataTypeConverter(options)
         val datatype = converter.convert(schemaInfo, dataTypes)

         // then:
         dataTypes.find("BarWithSuffix") shouldBeSameInstanceAs datatype

         datatype.shouldBeInstanceOf<StringEnumDataType>()
         datatype.getName().shouldBe("BarWithSuffix")
         datatype.getTypeName().shouldBe("BarWithSuffix")
     }

})
