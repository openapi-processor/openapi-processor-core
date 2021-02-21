/*
 * Copyright © 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.openapiprocessor.core.converter.mapping.EndpointTypeMapping
import io.openapiprocessor.core.converter.mapping.NullTypeMapping
import io.openapiprocessor.core.model.DataTypes
import io.openapiprocessor.core.model.HttpMethod
import io.openapiprocessor.core.model.datatypes.ObjectDataType
import io.openapiprocessor.core.support.getBodySchemaInfo
import io.openapiprocessor.core.support.parse

class DataTypeConverterNullSpec: StringSpec({

    val dataTypes = DataTypes()

    "wraps object property in a null wrapper if a null mappings exists" {
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
                 properties:
                   foo:
                     nullable: true
                     type: string
                 
        """.trimIndent())


        val options = ApiOptions()
        options.typeMappings = listOf(
            EndpointTypeMapping("/foo", listOf(
                NullTypeMapping(
                    "null",
                    "org.openapitools.jackson.nullable.JsonNullable"
                )
            ))
            )

        val schemaInfo = openApi.getBodySchemaInfo("Foo",
            "/foo", HttpMethod.PATCH, "application/json")

        // when:
        val converter = DataTypeConverter(options)
        val datatype = converter.convert(schemaInfo, dataTypes)

        // then:
        datatype.shouldBeInstanceOf<ObjectDataType>()
        val fooDataType = datatype.getObjectProperty("foo")
        fooDataType.getName().shouldBe("JsonNullable<String>")
    }

})
