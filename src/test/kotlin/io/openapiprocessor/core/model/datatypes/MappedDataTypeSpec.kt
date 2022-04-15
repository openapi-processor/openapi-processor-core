/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.model.datatypes

import io.kotest.core.spec.style.StringSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe

class MappedDataTypeSpec: StringSpec ({

    "get name of type with (optional) generic parameters" {
        class TypeName (val generics: List<String>, val typeName: String)

        withData(
            TypeName(emptyList(), "Foo"),
            TypeName(listOf("?"), "Foo<?>"),
            TypeName(listOf("java.lang.String"), "Foo<String>"),
            TypeName(listOf("java.lang.String", "java.lang.String"), "Foo<String, String>")
        ) { data ->
            val type = MappedDataType(
                "Foo",
                "model",
                data.generics.map { DataTypeName(it) })

            type.getTypeName() shouldBe data.typeName
        }
    }

    "get imports of type with (optional) generic parameters" {
        class TypeImports (val generics: List<String>, val imports: List<String>)

        withData(
            TypeImports(emptyList(), listOf("model.Foo")),
            TypeImports(listOf("?"), listOf("model.Foo")),
            TypeImports(
                listOf("java.lang.String"),
                listOf("model.Foo", "java.lang.String")),
            TypeImports(
                listOf("java.lang.String", "java.lang.String"),
                listOf("model.Foo", "java.lang.String"))
        ) { data ->
            val type = MappedDataType(
                "Foo",
                "model",
                data.generics.map { DataTypeName(it) })

            type.getImports() shouldBe data.imports
        }
    }
})
