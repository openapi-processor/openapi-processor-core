/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import io.openapiprocessor.core.converter.ApiOptions
import io.openapiprocessor.core.model.datatypes.InterfaceDataType
import java.io.Writer

class InterfaceDataTypeWriter(
    private val apiOptions: ApiOptions,
    private val headerWriter: SimpleWriter,
    private val javadocWriter: JavaDocWriter = JavaDocWriter()
) {

    fun write(target: Writer, dataType: InterfaceDataType) {
        headerWriter.write(target)
        target.write("package ${dataType.getPackageName()};\n\n")

        if (apiOptions.javadoc) {
            target.write(javadocWriter.convert(dataType))
        }

        target.write("public interface ${dataType.getTypeName()} {\n")
        target.write ("}\n")
    }
}
