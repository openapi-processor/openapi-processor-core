/*
 * Copyright © 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import com.github.hauner.openapi.core.writer.java.ApiWriter
import com.github.hauner.openapi.core.writer.java.DataTypeWriter
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.verify
import io.openapiprocessor.core.builder.api.`interface`
import io.openapiprocessor.core.converter.ApiOptions
import io.openapiprocessor.core.model.Api
import io.openapiprocessor.core.model.DataTypes
import io.openapiprocessor.core.model.Interface
import io.openapiprocessor.core.model.datatypes.*
import io.openapiprocessor.core.support.text
import io.openapiprocessor.core.tempFolder
import java.io.File
import java.io.Writer
import java.nio.file.Files
import java.nio.file.Path
import io.mockk.mockk as stub

@Suppress("BlockingMethodInNonBlockingContext")
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

    fun textOfApi(name: String): String {
        return options.getApiPath(name).text
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
            |enum Foo {
            |}
            |
            """.trimMargin()
    }

    "creates package structure in target folder" {
        // when:
        ApiWriter(options, stub(), stub(), stub())
            .write(Api())

        // then:
        val api = options.getSourceDir("api")
        val model = options.getSourceDir("model")

        Files.exists(api) shouldBe true
        Files.isDirectory(api) shouldBe true
        Files.exists(model) shouldBe true
        Files.isDirectory(model) shouldBe true
    }

    "does not fail if target folder structure already exists" {
        Files.createDirectories(options.getSourceDir("api"))
        Files.createDirectories(options.getSourceDir("model"))

        shouldNotThrowAny {
            ApiWriter (options, stub(), stub(), stub())
                .write (Api())
        }
    }

    "generates interface sources in api target folder" {
        val itfWriter = io.mockk.mockk<InterfaceWriter>()
        every { itfWriter.write(any(), any()) } answers {
            arg<Writer>(0).write("${arg<Interface>(1).name} interface!")
        }

        val api = Api(listOf(
            `interface`("Foo", options.getSourceDir("api").toString()) {},
            `interface`("Bar", options.getSourceDir("api").toString()) {}
        ))

        // when:
        ApiWriter (options, itfWriter, stub(), stub(), false)
            .write (api)

        // then:
        textOfApi("FooApi.java") shouldBe "Foo interface!"
        textOfApi("BarApi.java") shouldBe "Bar interface!"
    }


    "generates interface with valid java class name" {
        val itfWriter = io.mockk.mockk<InterfaceWriter>()
        every { itfWriter.write(any(), any()) } answers {
            arg<Writer>(0).write("${arg<Interface>(1).name} interface!")
        }

        val api = Api(listOf(
            `interface`("foo-bar", options.getSourceDir("api").toString()) {}
        ))

        // when:
        ApiWriter (options, itfWriter, stub(), stub(), false)
            .write (api)

        // then:
        Files.exists(options.getApiPath("FooBarApi.java")) shouldBe true
    }

    "generates model sources in model target folder" {
        val dtWriter = io.mockk.mockk<DataTypeWriter>()
        every { dtWriter.write(any(), any()) } answers {
            arg<Writer>(0).write("${arg<ObjectDataType>(1).getName()} class!")
        }

        val dt = DataTypes()
        dt.add(ObjectDataType("Foo", "${options.packageName}.model", linkedMapOf()))
        dt.add(ObjectDataType("Bar", "${options.packageName}.model", linkedMapOf()))
        val api = Api(models = dt)

        // when:
        ApiWriter (options, stub(), dtWriter, stub(), false)
            .write (api)

        // then:
        textOf("Foo.java") shouldBe "Foo class!"
        textOf("Bar.java") shouldBe "Bar class!"
    }


    "generates model for object data types only" {
        val dtWriter = io.mockk.mockk<DataTypeWriter>()

        val dt = DataTypes()
        dt.add(MappedDataType("Type", "${options.packageName}.model"))
        dt.add("simple", StringDataType())
        val api = Api(models = dt)

        // when:
        ApiWriter (options, stub(), dtWriter, stub(), false)
            .write (api)

        // then:
        verify(exactly = 0) {
            dtWriter.write (any(), any())
        }
    }

    "re-formats interface sources" {
        val itfWriter = io.mockk.mockk<InterfaceWriter>()
        every { itfWriter.write(any(), any()) } answers {
            arg<Writer>(0).write("  interface  \n ${arg<Interface>(1).name}    {    }\n")
        }

        // when:
        ApiWriter (options, itfWriter, stub(), stub(), true)
            .write (Api(listOf(
                `interface`("Foo", options.getSourceDir("api").toString()) {}
            )))

        // then:
        textOfApi("FooApi.java") shouldBe """
        |interface Foo {
        |}
        |
        """.trimMargin()
    }

    "re-formats model sources" {
        val dtWriter = io.mockk.mockk<DataTypeWriter>()
        every { dtWriter.write(any(), any()) } answers {
            arg<Writer>(0).write("  class \n  ${arg<ObjectDataType>(1).getName()} {   }\n")
        }

        val dt = DataTypes()
        dt.add(ObjectDataType("Foo", "${options.packageName}.model"))
        val api = Api(models = dt)

        // when:
        ApiWriter (options, stub(), dtWriter, stub(), true)
            .write (api)

        // then:
        textOf("Foo.java") shouldBe """
        |class Foo {
        |}
        |
        """.trimMargin()
    }

})


private fun ApiOptions.getApiPath(name: String): Path {
    return getSourcePath("api", name)
}

private fun ApiOptions.getModelPath(name: String): Path {
    return getSourcePath("model", name)
}

private fun ApiOptions.getSourcePath(pkg: String, name: String): Path {
    return getSourceDir(pkg)
        .resolve(name)
}

private fun ApiOptions.getSourceDir(pkg: String): Path {
    return Path.of(
        listOf(
            targetDir,
            packageName.replace(".", File.separator),
            pkg)
        .joinToString(File.separator))
}
