/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.string.shouldContain
import io.openapiprocessor.core.converter.ApiOptions
import io.openapiprocessor.core.extractImports
import io.openapiprocessor.core.model.datatypes.DataTypeName
import io.openapiprocessor.core.model.datatypes.InterfaceDataType
import java.io.StringWriter


class InterfaceDataTypeWriterSpec: StringSpec({
    isolationMode = IsolationMode.InstancePerTest

    val options = ApiOptions()
    options.oneOfInterface = true
    val generatedWriter = SimpleGeneratedWriter(options)

    val writer = InterfaceDataTypeWriter(options, generatedWriter)
    val target = StringWriter()

    "writes 'package'" {
        val pkg = "io.openapiprocessor.test"
        val dataType = InterfaceDataType (DataTypeName("Foo"), pkg)

        writer.write (target, dataType)

        target.toString() shouldContain
            """
            |package $pkg;
            |
            """.trimMargin()
    }

    "writes @Generated" {
        val dataType = InterfaceDataType (DataTypeName("Foo"), "pkg")

        writer.write(target, dataType)

        target.toString() shouldContain
            """
            |@Generated
            |public interface ${dataType.getTypeName()} {
            |}
            """.trimMargin()
    }

    "adds @Generated import" {
        val dataType = InterfaceDataType (DataTypeName("Foo"), "pkg")

        writer.write(target, dataType)

        extractImports(target).contains("")
    }

    "writes 'interface'" {
        val pkg = "io.openapiprocessor.test"
        val dataType = InterfaceDataType (DataTypeName("Foo"), pkg)

        writer.write (target, dataType)

        target.toString() shouldContain
            """
            |public interface ${dataType.getTypeName()} {
            |}
            |
            """.trimMargin()
    }
})
