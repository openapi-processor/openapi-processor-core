/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.string.shouldContain
import io.mockk.verify
import io.openapiprocessor.core.converter.ApiOptions
import io.openapiprocessor.core.model.datatypes.DataTypeName
import io.openapiprocessor.core.model.datatypes.InterfaceDataType
import java.io.StringWriter


class InterfaceDataTypeWriterSpec: StringSpec({
    isolationMode = IsolationMode.InstancePerTest

    val headerWriter: SimpleWriter = io.mockk.mockk(relaxed = true)
    val options = ApiOptions()
    options.oneOfInterface = true

    val writer = InterfaceDataTypeWriter(options, headerWriter)
    val target = StringWriter()

    "writes 'generated' comment" {
        val dataType = InterfaceDataType (DataTypeName("Foo"), "pkg")

        writer.write(target, dataType)

        verify (exactly = 1) {
            headerWriter.write(any())
        }
    }

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
