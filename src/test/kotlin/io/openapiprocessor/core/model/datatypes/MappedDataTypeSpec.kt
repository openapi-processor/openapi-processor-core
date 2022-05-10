/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.model.datatypes

import io.kotest.core.spec.style.StringSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe

class MappedDataTypeSpec: StringSpec ({
    class TypeName (val generics: List<String>, val typeName: String)

    withData(
        nameFn = {"get name of type with (optional) generic parameters: ${it.generics}"},

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

    class TypeImports (val generics: List<String>, val imports: List<String>)

    withData(
        nameFn = {"get imports of type with (optional) generic parameters: ${it.generics}"},

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

    class DataTypeNames (val generics: List<DataTypeName>, val typeName: String)

    withData(
        nameFn = {"get name of type with (optional) generic parameters & model name suffix: ${it.generics}"},

        DataTypeNames(listOf(
            DataTypeName("io.openapiprocessor.Bar", "io.openapiprocessor.BarSuffix")),
            "Foo<BarSuffix>"),
        DataTypeNames(listOf(
            DataTypeName("io.openapiprocessor.Bar", "io.openapiprocessor.BarSuffix"),
            DataTypeName("io.openapiprocessor.Bar", "io.openapiprocessor.BarSuffix")),
            "Foo<BarSuffix, BarSuffix>")
    ) { data ->
        val type = MappedDataType(
            "Foo",
            "model",
            data.generics)

        type.getTypeName() shouldBe data.typeName
    }

    class DataTypeImports (val generics: List<DataTypeName>, val imports: List<String>)

    withData(
        nameFn = {"get imports of type with (optional) generic parameters & model suffix: ${it.generics}"},

        DataTypeImports(listOf(
            DataTypeName("model.Bar", "model.BarSuffix")),
            listOf("model.Foo", "model.BarSuffix")),
        DataTypeImports(listOf(
            DataTypeName("model.Bar", "model.BarSuffix"),
            DataTypeName("model.Bar", "model.BarSuffix")),
            listOf("model.Foo", "model.BarSuffix"))
    ) { data ->
        val type = MappedDataType(
            "Foo",
            "model",
            data.generics)

        type.getImports() shouldBe data.imports
    }
})
