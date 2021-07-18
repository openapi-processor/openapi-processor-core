/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import io.openapiprocessor.core.converter.ApiOptions
import io.openapiprocessor.core.model.datatypes.DataType
import io.openapiprocessor.core.model.datatypes.ModelDataType
import io.openapiprocessor.core.model.datatypes.NullDataType
import io.openapiprocessor.core.support.capitalizeFirstChar
import java.io.Writer

private const val deprecated =  "@Deprecated"

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

        if (apiOptions.javadoc) {
            target.write(javadocWriter.convert(dataType))
        }

        if (dataType.deprecated) {
            target.write("$deprecated\n")
        }

        if (apiOptions.beanValidation) {
            val objectInfo = validationAnnotations.validate(dataType)
            if(objectInfo.hasAnnotations) {
                objectInfo.annotations.forEach {
                    target.write("$it\n")
                }
            }
        }

        target.write("public class ${dataType.getTypeName()} {\n\n")

        dataType.forEach { propName, propDataType ->
            val javaPropertyName = toCamelCase(propName)
            target.write(getProp(propName, javaPropertyName, propDataType,
                dataType.isRequired(propName)))
        }

        dataType.forEach { propName, propDataType ->
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

        if (apiOptions.javadoc) {
            result += javadocWriter.convert(propDataType)
        }

        result += ifDeprecated(propDataType)

        var propTypeName = propDataType.getTypeName()
        if(apiOptions.beanValidation) {
            val info = validationAnnotations.validate(propDataType, required)
            if (info.hasAnnotations) {
                result += "    ${info.annotations.joinToString(" ")}\n"
            }
            propTypeName = info.typeName
        }

        result += "    @JsonProperty(\"$propertyName\")\n"
        result += "    private $propTypeName $javaPropertyName"

        // null may have an init value
        if (propDataType is NullDataType && propDataType.init != null) {
            result += " = ${propDataType.init}"
        }

        result += ";\n\n"
        return result
    }

    private fun getGetter(propertyName: String, propDataType: DataType): String {
        var result = ""
        result += ifDeprecated(propDataType)

        result += """
            |    public ${propDataType.getTypeName()} get${propertyName.capitalizeFirstChar()}() {
            |        return ${propertyName};
            |    }
            |
            |
        """.trimMargin()

        return result
    }

    private fun getSetter(propertyName: String, propDataType: DataType): String {
        var result = ""
        result += ifDeprecated(propDataType)

        result += """
            |    public void set${propertyName.capitalizeFirstChar()}(${propDataType.getTypeName()} ${propertyName}) {
            |        this.${propertyName} = ${propertyName};
            |    }
            |
            |
        """.trimMargin()

        return result
    }

    private fun ifDeprecated(propDataType: DataType): String {
        return if (propDataType.deprecated) {
            "    $deprecated\n"
        } else {
            ""
        }
    }

    private fun collectImports(packageName: String, dataType: ModelDataType): List<String> {
        val imports = mutableSetOf<String>()

        dataType.forEach { _, _ ->
            imports.add("com.fasterxml.jackson.annotation.JsonProperty")
        }

        imports.addAll(dataType.referencedImports)

        if (apiOptions.beanValidation) {
            val objectInfo = validationAnnotations.validate(dataType)
            if (objectInfo.hasAnnotations) {
                imports.addAll(objectInfo.imports)
            }

            dataType.forEach { propName, propDataType ->
                val propInfo = validationAnnotations.validate(
                    propDataType, dataType.isRequired(propName))

                imports.addAll(propInfo.imports)
            }
        }

        return DefaultImportFilter()
            .filter(packageName, imports)
            .sorted()
    }

}
