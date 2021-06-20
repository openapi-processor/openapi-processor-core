/*
 * Copyright © 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.types.shouldBeInstanceOf
import io.openapiprocessor.core.model.DataTypes
import io.openapiprocessor.core.model.HttpMethod
import io.openapiprocessor.core.model.datatypes.AllOfObjectDataType
import io.openapiprocessor.core.support.getBodySchemaInfo
import io.openapiprocessor.core.support.parse

class DataTypeConverterAllOfRequiredSpec: StringSpec({

    val dataTypes = DataTypes()

    "applies required to allOf composition" {
        val openApi = parse("""
           openapi: 3.0.2
           info:
             title: API
             version: 1.0.0

           paths:
             /foo:
               patch:
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
                 allOf:
                   - type: object
                     required: [ foo, bar ]
                     properties:
                       foo:
                         type: string
                       bar:
                         type: string
                   - type: object
                     required: [ baz, qux ]
                     properties:
                       baz:
                         type: string
                       qux:
                         type: string

        """.trimIndent())


        val options = ApiOptions()

        val schemaInfo = openApi.getBodySchemaInfo("Foo",
                "/foo", HttpMethod.PATCH, "application/json")

        // when:
        val converter = DataTypeConverter(options)
        val datatype = converter.convert(schemaInfo, dataTypes)

        // then:
        datatype.shouldBeInstanceOf<AllOfObjectDataType>()
        datatype.constraints!!.required shouldContainAll listOf("foo", "bar", "baz", "qux")
    }

})
