/*
 * Copyright 2019-2020 the original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.openapiprocessor.core.writer.java

import io.openapiprocessor.core.model.datatypes.DataType
import io.openapiprocessor.core.model.datatypes.StringEnumDataType
import java.io.Writer

/**
 * Writer for String enum.
 *
 * @author Martin Hauner
 */
open class StringEnumWriter(private val headerWriter: SimpleWriter) {

    fun write(target: Writer, dataType: StringEnumDataType) {
        headerWriter.write(target)
        target.write("package ${dataType.getPackageName()};\n\n")

        val imports = collectImports (dataType.getPackageName(), dataType)
        imports.forEach {
            target.write("import ${it};\n")
        }
        if(imports.isNotEmpty()) {
            target.write("\n")
        }

        target.write("public enum ${dataType.getTypeName()} {\n")

        val values = mutableListOf<String>()
        dataType.values.forEach {
            values.add ("    ${toEnum (it)}(\"${it}\")")
        }
        target.write (values.joinToString (",\n") + ";\n\n")
        target.write("    private final String value;\n\n")

        target.write (
            """
            |    private ${dataType.getTypeName()}(String value) {
            |        this.value = value;
            |    }
            |
            |
            """.trimMargin())

        target.write(
            """
            |    @JsonValue
            |    public String getValue() {
            |        return this.value;
            |    }
            |
            |
            """.trimMargin())

        target.write(
            """
            |    @JsonCreator
            |    public static ${dataType.getTypeName()} fromValue(String value) {
            |        for (${dataType.getTypeName()} val : ${dataType.getTypeName()}.values()) {
            |            if (val.value.equals(value)) {
            |                return val;
            |            }
            |        }
            |        throw new IllegalArgumentException(value);
            |    }
            |
            |
            """.trimMargin())

        target.write ("}\n")
    }

    private fun collectImports(packageName: String, dataType: DataType): List<String> {
        val imports = mutableSetOf<String>()
        imports.add ("com.fasterxml.jackson.annotation.JsonCreator")
        imports.add ("com.fasterxml.jackson.annotation.JsonValue")
        imports.addAll (dataType.referencedImports)

        return DefaultImportFilter ()
            .filter(packageName, imports)
            .sorted()
    }

}
