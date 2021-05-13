/*
 * Copyright © 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.string.shouldContain
import io.mockk.mockk
import io.openapiprocessor.core.converter.ApiOptions
import io.openapiprocessor.core.extractImports
import io.openapiprocessor.core.model.datatypes.DataTypeConstraints
import io.openapiprocessor.core.support.datatypes.ObjectDataType
import io.openapiprocessor.core.model.datatypes.StringDataType
import java.io.StringWriter

class DataTypeWriterSpec: StringSpec({
    val headerWriter: SimpleWriter = mockk(relaxed = true)
    val options = ApiOptions()

    val writer = DataTypeWriter(options, headerWriter, BeanValidationFactory())
    val target = StringWriter()

    "writes @NotNull import for required property" {
        options.beanValidation = true

        val dataType = ObjectDataType("Foo", "pkg", linkedMapOf(
            Pair("foo", StringDataType())
        ), DataTypeConstraints(required = listOf("foo")), false)

        // when:
        writer.write(target, dataType)

        // then:
        val imports = extractImports(target)
        imports shouldContain "import javax.validation.constraints.NotNull;"
    }

    "writes required property with @NotNull annotation" {
        options.beanValidation = true

        val dataType = ObjectDataType("Foo", "pkg", linkedMapOf(
            Pair("foo", StringDataType())
        ), DataTypeConstraints(required = listOf("foo")), false)

        // when:
        writer.write(target, dataType)

        // then:
        target.toString() shouldContain
            """    
            |    @NotNull
            |    @JsonProperty("foo")
            |    private String foo;
            |
            """.trimMargin()
    }

})
