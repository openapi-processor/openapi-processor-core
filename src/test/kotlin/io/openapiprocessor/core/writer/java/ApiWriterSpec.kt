/*
 * Copyright © 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import com.github.hauner.openapi.core.writer.java.ApiWriter
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.openapiprocessor.core.converter.ApiOptions
import io.openapiprocessor.core.model.Api
import io.openapiprocessor.core.model.DataTypes
import io.openapiprocessor.core.model.datatypes.StringEnumDataType
import io.openapiprocessor.core.support.text
import io.openapiprocessor.core.tempFolder
import java.io.File
import java.io.Writer
import java.nio.file.Path
import io.mockk.mockk as stub

class ApiWriterSpec: StringSpec({
    isolationMode = IsolationMode.InstancePerTest

    val target = tempFolder()
    val options = ApiOptions()

    beforeTest {
        options.packageName = "io.openapiprocessor.test"
        options.targetDir = listOf(target.toString(), "java", "src").joinToString(File.separator)
    }

    fun textOf(name: String): String {
        return options.getModelPath(name).text
    }

    "generates model enum source files in model target folder" {
        val enumWriter = stub<StringEnumWriter>()
        every { enumWriter.write(any(), any()) }
            .answers {
                firstArg<Writer>().write("Foo enum!\n")
            }
            .andThen {
                firstArg<Writer>().write("Bar enum!\n")
            }

        val dts = DataTypes()
        dts.add(StringEnumDataType("Foo", "${options.packageName}.model"))
        dts.add(StringEnumDataType("Bar", "${options.packageName}.model"))
        val api = Api(models = dts)

        // when:
        ApiWriter(options, stub(), stub(), enumWriter, false)
            .write(api)

        // then:
        textOf("Foo.java") shouldBe "Foo enum!\n"
        textOf("Bar.java") shouldBe "Bar enum!\n"
    }

    "re-formats model enum source file" {
        val enumWriter = stub<StringEnumWriter>()
        every { enumWriter.write(any(), any()) }
            .answers {
                firstArg<Writer>().write("    enum   Foo   {   }    ")
            }

        val dts = DataTypes()
        dts.add (StringEnumDataType("Foo", "${options.packageName}.model"))
        val api = Api(models = dts)

        // when:
        ApiWriter(options, stub(), stub(), enumWriter, true)
            .write(api)

        // then:
        textOf("Foo.java") shouldBe """
            enum Foo {
            }

            """.trimIndent()
    }

})


private fun ApiOptions.getModelPath(name: String): Path {
    return getSourcePath("model", name)
}

private fun ApiOptions.getSourcePath(pkg: String, name: String): Path {
    return Path.of(listOf(targetDir, packageName.replace(".", File.separator), pkg, name)
        .joinToString(File.separator))
}
