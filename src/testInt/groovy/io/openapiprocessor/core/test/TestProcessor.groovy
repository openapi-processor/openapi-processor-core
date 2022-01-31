/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.test

import io.openapiprocessor.core.converter.ApiConverter
import io.openapiprocessor.core.converter.ApiOptions
import io.openapiprocessor.core.converter.OptionsConverter
import io.openapiprocessor.core.framework.FrameworkBase
import io.openapiprocessor.core.parser.OpenApi
import io.openapiprocessor.core.parser.Parser
import io.openapiprocessor.core.writer.java.ApiWriter
import io.openapiprocessor.core.writer.java.BeanValidationFactory
import io.openapiprocessor.core.writer.java.DataTypeWriter
import io.openapiprocessor.core.writer.java.DefaultImportFilter
import io.openapiprocessor.core.writer.java.InterfaceWriter
import io.openapiprocessor.core.writer.java.JavaDocWriter
import io.openapiprocessor.core.writer.java.MethodWriter
import io.openapiprocessor.core.writer.java.StringEnumWriter
import groovy.util.logging.Slf4j
import io.openapiprocessor.api.v1.OpenApiProcessor

/**
 *  Simple processor for testing.
 */
@Slf4j
class TestProcessor implements OpenApiProcessor {

    @Override
    String getName () {
        'test'
    }

    @Override
    void run (Map<String, ?> processorOptions) {
        try {
            def parser = new Parser ()
            OpenApi openapi = parser.parse (processorOptions)
            if (processorOptions.showWarnings) {
                openapi.printWarnings ()
            }

            def framework = new FrameworkBase()
            def annotations = new TestFrameworkAnnotations()

            def options = convertOptions (processorOptions)
            def cv = new ApiConverter(options, framework)
            def api = cv.convert (openapi)

            def headerWriter = new TestHeaderWriter()
            def beanValidationFactory = new BeanValidationFactory()

            def writer = new ApiWriter (
                options,
                new InterfaceWriter(
                    options,
                    headerWriter,
                    new MethodWriter(options,
                        new TestMappingAnnotationWriter (),
                        new TestParameterAnnotationWriter (),
                        beanValidationFactory,
                        new JavaDocWriter()),
                    annotations,
                    beanValidationFactory,
                    new DefaultImportFilter()
                ),
                new DataTypeWriter(
                    options,
                    headerWriter,
                    beanValidationFactory,
                    new JavaDocWriter()
                ),
                new StringEnumWriter(headerWriter)
            )

            writer.write (api)
        } catch (Exception e) {
            log.error ("processing failed!", e)
            throw e
        }
    }

    private static ApiOptions convertOptions (Map<String, ?> processorOptions) {
        def options = new OptionsConverter().convertOptions (processorOptions)
        options.validate ()
        options
    }

}
