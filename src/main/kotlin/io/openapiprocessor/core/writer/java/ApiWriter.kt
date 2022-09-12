/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import com.google.googlejavaformat.java.Formatter
import com.google.googlejavaformat.java.JavaFormatterOptions
import io.openapiprocessor.core.converter.ApiOptions
import io.openapiprocessor.core.model.Api
import io.openapiprocessor.core.model.Interface
import io.openapiprocessor.core.model.datatypes.InterfaceDataType
import io.openapiprocessor.core.model.datatypes.StringEnumDataType
import io.openapiprocessor.core.model.datatypes.ModelDataType
import io.openapiprocessor.core.support.toURI
import java.io.BufferedWriter
import java.io.StringWriter
import java.io.Writer
import java.nio.file.Files
import java.nio.file.Path
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.file.Paths

/**
 * Root writer for the generated api files.
 */
class ApiWriter(
    private val options: ApiOptions,
    private val interfaceWriter: InterfaceWriter,
    private val dataTypeWriter: DataTypeWriter,
    private val enumWriter: StringEnumWriter,
    private val interfaceDataTypeWriter: InterfaceDataTypeWriter,
    private val fileHandler: FileHandler
) {
    private var log: Logger = LoggerFactory.getLogger(this.javaClass.name)

    private var formatter: Formatter? = null

    init {
        initFormatter()
    }

    fun write(api: Api) {
        fileHandler.createTargetFolders()
        writeInterfaces(api)
        writeObjectDataTypes(api)
        writeInterfaceDataTypes(api)
        writeEnumDataTypes(api)
    }

    private fun writeInterfaces(api: Api) {
        api.forEachInterface {
            val writer = fileHandler.createApiWriter(it.getPackageName(), it.getInterfaceName())
            writeInterface(writer, it)
            writer.close()
        }
    }

    private fun writeObjectDataTypes(api: Api) {
        api.forEachModelDataType {
            val writer = fileHandler.createModelWriter(it.getPackageName(), it.getTypeName())
            writeDataType(writer, it)
            writer.close()
        }
    }

    private fun writeInterfaceDataTypes(api: Api) {
        api.forEachInterfaceDataType() {
            val writer = fileHandler.createModelWriter(it.getPackageName(), it.getTypeName())
            writeDataType(writer, it)
            writer.close()
        }
    }

    private fun writeEnumDataTypes(api: Api) {
        api.forEachEnumDataType {
            val writer = fileHandler.createModelWriter(it.getPackageName(), it.getTypeName())
            writeEnumDataType(writer, it)
            writer.close()
        }
    }

    private fun writeInterface(writer: Writer, itf: Interface) {
        val raw = StringWriter()
        interfaceWriter.write(raw, itf)
        writer.write(format(raw.toString()))
    }

    private fun writeDataType(writer: Writer, dataType: ModelDataType) {
        val raw = StringWriter()
        dataTypeWriter.write(raw, dataType)
        writer.write(format(raw.toString ()))
    }

    private fun writeDataType(writer: Writer, dataType: InterfaceDataType) {
        val raw = StringWriter()
        interfaceDataTypeWriter.write(raw, dataType)
        writer.write(format(raw.toString ()))
    }

    private fun writeEnumDataType(writer: Writer, enumDataType: StringEnumDataType) {
        val raw = StringWriter()
        enumWriter.write(raw, enumDataType)
        writer.write(format(raw.toString()))
    }

    private fun format(raw: String): String {
        try {
            if (formatter == null) {
                return raw
            }

            return correctLineFeed(formatter!!.formatSource(raw))
        } catch (e: Exception) {
            throw FormattingException(raw, e)
        }
    }

    // put line feed before last closing }
    private fun correctLineFeed(formatted: String): String {
        val index = formatted.lastIndexOf("}")

        return StringBuilder()
            .append(formatted.substring(0, index))
            .append("\n}\n")
            .toString()
      }

    private fun initFormatter() {
        if (options.formatCode) {
            formatter = Formatter(
                JavaFormatterOptions
                    .builder()
                    .style(JavaFormatterOptions.Style.AOSP)
                    .build())
        }
    }

}
