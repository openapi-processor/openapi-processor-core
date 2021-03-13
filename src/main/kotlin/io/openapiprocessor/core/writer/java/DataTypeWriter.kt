/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import io.openapiprocessor.core.converter.ApiOptions
import io.openapiprocessor.core.model.datatypes.DataType
import io.openapiprocessor.core.model.datatypes.ModelDataType
import io.openapiprocessor.core.model.datatypes.NullDataType
import java.io.Writer

/**
 * Writer for POJO classes.
 */
class DataTypeWriter(
    private val apiOptions: ApiOptions,
    private val headerWriter: SimpleWriter,
    private val validationAnnotations: BeanValidationFactory = BeanValidationFactory(),
    private val javadocWriter: JavaDocWriter = JavaDocWriter()
) {

    fun write(target: Writer, dataType: ModelDataType) {
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

        // add description
        if (apiOptions.javadoc) {
            target.write(
                javadocWriter.convert(dataType)
            )
        }

        target.write("public class ${dataType.getName()} {\n\n")

        val properties = dataType.getProperties()
        properties.forEach { (propName, propDataType) ->
            val javaPropertyName = toCamelCase(propName)
            target.write(getProp(propName, javaPropertyName, propDataType,
                dataType.isRequired(propName)))
        }

        properties.forEach { (propName, propDataType) ->
            val javaPropertyName = toCamelCase(propName)
            target.write(getGetter(javaPropertyName, propDataType))
            target.write(getSetter(javaPropertyName, propDataType))
        }

        target.write ("}\n")
    }

    private fun getProp(
        propertyName: String, javaPropertyName: String,
        propDataType: DataType, required: Boolean): String {

        var result = ""
        if (propDataType.isDeprecated()) {
            result += "    @Deprecated\n"
        }

        if(apiOptions.beanValidation) {
            val annotations = validationAnnotations.createAnnotations(propDataType, required)
            if (annotations.isNotEmpty()) {
                result += "    $annotations\n"
            }
        }

        result += "    @JsonProperty(\"${propertyName}\")\n"
        result += "    private ${propDataType.getName()} ${javaPropertyName}"

        // null may have an init value
        if (propDataType is NullDataType && propDataType.init != null) {
            result += " = ${propDataType.init}"
        }

        result += ";\n\n"
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

    private fun collectImports(packageName: String, dataType: ModelDataType): List<String> {
        val imports = mutableSetOf<String>()

        if (dataType.getProperties().isNotEmpty()) {
            imports.add("com.fasterxml.jackson.annotation.JsonProperty")
        }

        imports.addAll(dataType.getReferencedImports())

        if (apiOptions.beanValidation) {
            val properties = dataType.getProperties()
            properties.forEach { (propName, propDataType) ->
                imports.addAll(validationAnnotations.collectImports(
                        propDataType, dataType.isRequired(propName)))
            }
        }

        return DefaultImportFilter()
            .filter(packageName, imports)
            .sorted()
    }

}
