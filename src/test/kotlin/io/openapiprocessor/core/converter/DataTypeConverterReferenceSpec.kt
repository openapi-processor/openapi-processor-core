/*
 * Copyright © 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.openapiprocessor.core.converter.mapping.TypeMapping
import io.openapiprocessor.core.model.DataTypes
import io.openapiprocessor.core.model.HttpMethod.GET
import io.openapiprocessor.core.support.getSchemaInfo
import io.openapiprocessor.core.support.parse

class DataTypeConverterReferenceSpec: StringSpec({
    isolationMode = IsolationMode.InstancePerTest

    val dataTypes = DataTypes()

    "detect usage of normal array item schema" {
        val openApi = parse("""
           openapi: 3.0.2
           info:
             title: API
             version: 1.0.0
           
           paths:
             /foo:
               get:
                 responses:
                   '200':
                     description: ...
                     content:
                       application/json:
                         schema:
                           ${'$'}ref: '#/components/schemas/FooArray'
           
           components:
             schemas:
                       
               FooArray:
                 description: a Foo array
                 type: array
                 items: 
                   ${'$'}ref: '#/components/schemas/Foo'
           
               Foo:
                 description: a Foo
                 type: object
                 properties:
                   foo:
                     type: string
                 
        """.trimIndent())

        val schemaInfo = openApi.getSchemaInfo("FooResponse200",
            "/foo", GET, "200", "application/json")

        // when:
        val converter = DataTypeConverter(ApiOptions())
        converter.convert(schemaInfo, dataTypes)

        // then:
        dataTypes.size shouldBe 1
        dataTypes.getRefCnt("Foo") shouldBe 1
    }

    "detect usage of array item schema that is only used in a mapping" {
        val openApi = parse ("""
           openapi: 3.0.2
           info:
             title: API
             version: 1.0.0

           paths:
             /foo:
               get:
                 responses:
                   '200':
                     description: ...
                     content:
                       application/json:
                         schema:
                           ${'$'}ref: '#/components/schemas/FooArray'

           components:
             schemas:

               FooArray:
                 description: a Foo array
                 type: array
                 items: 
                   ${'$'}ref: '#/components/schemas/Foo'

               Foo:
                 description: a Foo
                 type: object
                 properties:
                   foo:
                     type: string

        """.trimIndent())

        val options = ApiOptions()
        options.typeMappings = listOf(
            TypeMapping(
                "FooArray",
                "io.openapiprocessor.test.Mapped",
                listOf("io.openapiprocessor.generated.model.Foo")
            )
        )

        val schemaInfo = openApi.getSchemaInfo("FooResponse200",
            "/foo", GET, "200", "application/json")

        // when:
        val converter = DataTypeConverter(options)
        converter.convert(schemaInfo, dataTypes)

        // then:
        dataTypes.size shouldBe 1
        dataTypes.getRefCnt("Foo") shouldBe 1
    }

    "detect usage of object schema & properties" {
        val openApi = parse ("""
           openapi: 3.0.2
           info:
             title: API
             version: 1.0.0

           paths:
             /foo:
               get:
                 responses:
                   '200':
                     description: ...
                     content:
                       application/json:
                         schema:
                           ${'$'}ref: '#/components/schemas/Foo'

           components:
             schemas:

               Foo:
                 description: a Foo
                 type: object
                 properties:
                   foo:
                     type: string
                   bar:
                     ${'$'}ref: '#/components/schemas/Bar'

               Bar:
                 description: a Bar
                 type: object
                 properties:
                   bar:
                     type: string

        """.trimIndent())

        val options = ApiOptions()
        options.typeMappings = listOf(
            TypeMapping(
                "FooArray",
                "io.openapiprocessor.test.Mapped",
                listOf("io.openapiprocessor.generated.model.Foo")
            )
        )

        val schemaInfo = openApi.getSchemaInfo("FooResponse200",
            "/foo", GET, "200", "application/json")

        // when:
        val converter = DataTypeConverter(options)
        converter.convert(schemaInfo, dataTypes)

        // then:
        dataTypes.size shouldBe 2
        dataTypes.getRefCnt("Foo") shouldBe 1
        dataTypes.getRefCnt("Bar") shouldBe 1
    }




    // array normal - done
    // array mapped - done
    // object normal - done

    // object mapped
    // object mapped, generics
    // mapped collection
    // mapped data type
    // mapped data type, generics
    // package name
})

/*
   "generates model if it is only referenced in the mapping of a composed type" {
         val openApi = parse ("""
            openapi: 3.0.2
            info:
              title: API
              version: 1.0.0

            paths:
              /foo:
                get:
                  responses:
                    '200':
                      description: ...
                      content:
                        application/json:
                          schema:
                            ${'$'}ref: '#/components/schemas/ComposedFoo'

            components:
              schemas:

                ComposedFoo:
                  description: ...
                  allOf:
                    - ${'$'}ref: '#/components/schemas/Foo'

                Foo:
                  description: a Foo
                  type: object
                  properties:
                    foo:
                      type: string

         """.trimIndent())

        val options = ApiOptions()
        options.typeMappings = listOf(
            TypeMapping(
                "ComposedFoo",
                "io.openapiprocessor.test.Wrapped",
                listOf("io.openapiprocessor.generated.model.Foo")
            )
        )

        val api: Api = ApiConverter (options, FrameworkBase())
            .convert(openApi)

        api.getDataTypes().getModelDataTypes().size shouldBe 1
     }
 */
