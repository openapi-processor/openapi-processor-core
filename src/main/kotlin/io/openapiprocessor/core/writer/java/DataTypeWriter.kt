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

import io.openapiprocessor.core.converter.ApiOptions
import io.openapiprocessor.core.model.datatypes.DataType
import io.openapiprocessor.core.model.datatypes.ObjectDataType
import java.io.Writer

/**
 * Writer for POJO classes.
 *
 * @author Martin Hauner
 * @author Bastian Wilhelm
 */
class DataTypeWriter(
    private val apiOptions: ApiOptions,
    private val headerWriter: SimpleWriter,
    private val validationAnnotations: BeanValidationFactory = BeanValidationFactory()
) {

    fun write(target: Writer, dataType: ObjectDataType) {
        headerWriter.write(target)
        target.write("package ${dataType.getPackageName()};\n\n")

        val imports: List<String> = collectImports(dataType.getPackageName(), dataType)
        imports.forEach {
            target.write("import ${it};\n")
        }

        if (imports.isNotEmpty()) {
            target.write("\n")
        }

        if (dataType.isDeprecated()) {
            target.write("@Deprecated\n")
        }

        target.write("public class ${dataType.getName()} {\n\n")

        val propertyNames = dataType.getObjectProperties().keys
        propertyNames.forEach {
            val javaPropertyName = toCamelCase(it)
            val propDataType = dataType.getObjectProperty(it)
            target.write(getProp(it, javaPropertyName, propDataType))
        }

        propertyNames.forEach {
            val javaPropertyName = toCamelCase(it)
            val propDataType = dataType.getObjectProperty(it)
            target.write(getGetter(javaPropertyName, propDataType))
            target.write(getSetter(javaPropertyName, propDataType))
        }

        target.write ("}\n")
    }

    private fun getProp(propertyName: String, javaPropertyName: String, propDataType: DataType): String  {
        var result = ""
        if (propDataType.isDeprecated()) {
            result += "    @Deprecated\n"
        }

        result += "    @JsonProperty(\"${propertyName}\")\n"

        if(apiOptions.beanValidation) {
            val beanValidationAnnotations = validationAnnotations.createAnnotations(propDataType)
            if (beanValidationAnnotations.isNotEmpty()) {
                result += "    $beanValidationAnnotations\n"
            }
        }

        result += "    private ${propDataType.getName()} ${javaPropertyName};\n\n"
        return result
    }

    private fun getGetter(propertyName: String, propDataType: DataType): String {
        var result = ""
        if (propDataType.isDeprecated()) {
            result += "    @Deprecated\n"
        }

        result += """
            |    public ${propDataType.getName()} get${propertyName.capitalize()}() {
            |        return ${propertyName};
            |    }
            |
            |
        """.trimMargin()

        return result
    }

    private fun getSetter(propertyName: String, propDataType: DataType): String {
        var result = ""
        if (propDataType.isDeprecated()) {
            result += "    @Deprecated\n"
        }

        result += """
            |    public void set${propertyName.capitalize()}(${propDataType.getName()} ${propertyName}) {
            |        this.${propertyName} = ${propertyName};
            |    }
            |
            |
        """.trimMargin()

        return result
    }

    private fun collectImports(packageName: String, dataType: ObjectDataType): List<String> {
        val imports = mutableSetOf<String>()
        imports.add("com.fasterxml.jackson.annotation.JsonProperty")

        imports.addAll(dataType.getReferencedImports())

        if (apiOptions.beanValidation) {
            dataType.getObjectProperties().values.forEach {
                imports.addAll(validationAnnotations.collectImports(it))
            }
        }

        return DefaultImportFilter()
            .filter(packageName, imports)
            .sorted()
    }

}
