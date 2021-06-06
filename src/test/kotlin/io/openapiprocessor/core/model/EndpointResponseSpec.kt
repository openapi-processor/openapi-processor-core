/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.model

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.openapiprocessor.core.model.datatypes.AnyOneOfObjectDataType
import io.openapiprocessor.core.model.datatypes.DataTypeName
import io.openapiprocessor.core.model.datatypes.ObjectDataType
import io.openapiprocessor.core.model.datatypes.StringDataType
import io.openapiprocessor.core.processor.mapping.v2.ResultStyle

class EndpointResponseSpec: StringSpec({

    "anyOf/oneOf always uses multi response" {
        val er = EndpointResponse(
            Response("", AnyOneOfObjectDataType("Foo", "pkg", "anyOf")),
            emptySet()
        )

        er.getResponseType(ResultStyle.ALL) shouldBe "Object"
        er.getResponseImports(ResultStyle.ALL) shouldBe emptySet()
        er.getResponseType(ResultStyle.SUCCESS) shouldBe "Object"
        er.getResponseImports(ResultStyle.SUCCESS) shouldBe emptySet()
    }

    "result style all with errors uses multi response" {
        val er = EndpointResponse(
            Response("", StringDataType()),
            setOf(Response("text/plain", StringDataType()))
        )

        er.getResponseType(ResultStyle.ALL) shouldBe "Object"
        er.getResponseImports(ResultStyle.ALL) shouldBe emptySet()
    }

    "result style all without errors uses single response" {
        val er = EndpointResponse(
            Response("", ObjectDataType(DataTypeName("Foo"), "pkg", linkedMapOf(
                "bar" to StringDataType()
            ))),
            emptySet()
        )

        er.getResponseType(ResultStyle.ALL) shouldBe "Foo"
        er.getResponseImports(ResultStyle.ALL) shouldBe setOf("pkg.Foo")
    }

    "result style single uses single response" {
        val er = EndpointResponse(
            Response("", ObjectDataType(DataTypeName("Foo"), "pkg", linkedMapOf(
                "bar" to StringDataType()
            ))),
            setOf(Response("text/plain", StringDataType()))
        )

        er.getResponseType(ResultStyle.SUCCESS) shouldBe "Foo"
        er.getResponseImports(ResultStyle.SUCCESS) shouldBe setOf("pkg.Foo")
    }

})
