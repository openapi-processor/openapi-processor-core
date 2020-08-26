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

package com.github.hauner.openapi.processor.core.processor.test

import com.github.hauner.openapi.core.converter.ApiConverter
import io.openapiprocessor.core.converter.ApiOptions
import com.github.hauner.openapi.core.framework.FrameworkBase
import io.openapiprocessor.core.parser.OpenApi
import io.openapiprocessor.core.parser.Parser
import io.openapiprocessor.core.processor.MappingConverter
import io.openapiprocessor.core.processor.MappingReader
import com.github.hauner.openapi.core.writer.java.ApiWriter
import com.github.hauner.openapi.core.writer.java.BeanValidationFactory
import com.github.hauner.openapi.core.writer.java.DataTypeWriter
import com.github.hauner.openapi.core.writer.java.InterfaceWriter
import com.github.hauner.openapi.core.writer.java.MethodWriter
import com.github.hauner.openapi.core.writer.java.StringEnumWriter
import groovy.util.logging.Slf4j
import io.openapiprocessor.api.OpenApiProcessor

/**
 *  Simple processor for testing.
 *
 *  @author Martin Hauner
 *  @author Bastian Wilhelm
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

            def writer = new ApiWriter (options,
                new InterfaceWriter(
                    headerWriter: headerWriter,
                    methodWriter: new MethodWriter(
                        mappingAnnotationWriter: new TestMappingAnnotationWriter (),
                        parameterAnnotationWriter: new TestParameterAnnotationWriter (),
                        beanValidationFactory: beanValidationFactory,
                        apiOptions: options
                    ),
                    beanValidationFactory: beanValidationFactory,
                    annotations: annotations,
                    apiOptions: options
                ),
                new DataTypeWriter(
                    headerWriter: headerWriter,
                    beanValidationFactory: beanValidationFactory,
                    apiOptions: options
                ),
                new StringEnumWriter(headerWriter: headerWriter)
            )

            writer.write (api)
        } catch (Exception e) {
            log.error ("processing failed!", e)
            throw e
        }
    }

    private static ApiOptions convertOptions (Map<String, ?> processorOptions) {
        def reader = new MappingReader ()
        def converter = new MappingConverter ()
        def mapping

        if (processorOptions.containsKey ('mapping')) {
            mapping = reader.read (processorOptions.mapping as String)

        } else {
            log.error ("error: missing 'mapping' configuration!")
        }

        def options = new ApiOptions ()
        options.apiPath = processorOptions.apiPath
        options.targetDir = processorOptions.targetDir

        if (mapping) {
            if (mapping?.options?.packageName != null) {
                options.packageName = mapping.options.packageName
            } else {
                log.warn ("no 'options:package-name' set in mapping!")
            }

            if (mapping?.options?.beanValidation != null) {
                options.beanValidation = mapping.options.beanValidation
            }

            options.typeMappings = converter.convert (mapping)
        }

        options
    }

}
