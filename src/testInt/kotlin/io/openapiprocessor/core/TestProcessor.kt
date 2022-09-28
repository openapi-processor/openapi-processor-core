/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core

import io.openapiprocessor.api.v1.OpenApiProcessor
import io.openapiprocessor.core.converter.ApiConverter
import io.openapiprocessor.core.converter.ApiOptions
import io.openapiprocessor.core.converter.OptionsConverter
import io.openapiprocessor.core.framework.FrameworkBase
import io.openapiprocessor.core.parser.Parser
import io.openapiprocessor.core.writer.java.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 *  Simple processor for testing.
 */
class TestProcessor: OpenApiProcessor {
    val log: Logger = LoggerFactory.getLogger(this.javaClass.name)

    override fun getName(): String {
        return "test"
    }

    override fun run(processorOptions: MutableMap<String, *>) {
        try {
            val parser = Parser ()
            val openapi = parser.parse(processorOptions)
            if (processorOptions.containsKey("showWarnings")) {
                openapi.printWarnings()
            }

            val options = convertOptions(processorOptions)
            val cv = ApiConverter(options, FrameworkBase())
            val api = cv.convert(openapi)

            val generatedInfo = GeneratedInfo(
                "openapi-processor-core",
                "test",
//                OffsetDateTime.now().toString()
//                url = "https://github.com/openapi-processor/openapi-processor-core"
            )

            val generatedWriter = GeneratedWriterImpl(generatedInfo, options)
            val beanValidation = BeanValidationFactory()
            val javaDocWriter = JavaDocWriter()

            val writer = ApiWriter(
                options,
                generatedWriter,
                InterfaceWriter(
                    options,
                    generatedWriter,
                    MethodWriter(
                        options,
                        TestProcessorMappingAnnotationWriter(),
                        TestProcessorParameterAnnotationWriter(),
                        beanValidation,
                        JavaDocWriter()
                    ),
                    TestFrameworkAnnotations(),
                    beanValidation,
                    DefaultImportFilter()
                ),
                DataTypeWriter(
                    options,
                    generatedWriter,
                    beanValidation,
                    javaDocWriter
                ),
                StringEnumWriter(generatedWriter),
                InterfaceDataTypeWriter(
                    options,
                    generatedWriter,
                    javaDocWriter
                )
            )

            writer.write(api)
        } catch (e: Exception) {
            log.error ("processing failed!", e)
            throw e
        }
    }
}

private fun convertOptions(processorOptions: MutableMap<String, *>): ApiOptions {
    val target = mutableMapOf<String, Any>()
    processorOptions.forEach {(key, value) ->
        target[key] = value!!
    }

    val options = OptionsConverter().convertOptions(target)
    options.validate()
    return options
}
