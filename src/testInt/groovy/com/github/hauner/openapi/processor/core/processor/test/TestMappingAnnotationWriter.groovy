/*
 * Copyright 2020 the original authors
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

import io.openapiprocessor.core.model.Endpoint
import io.openapiprocessor.core.model.EndpointResponse
import com.github.hauner.openapi.core.writer.java.MappingAnnotationWriter

/**
 * simple {@link MappingAnnotationWriter} implementation for testing.
 *
 * @author Martin Hauner
 */
class TestMappingAnnotationWriter implements MappingAnnotationWriter {

    @Override
    void write (Writer target, Endpoint endpoint, EndpointResponse endpointResponse) {
        target.write ("""${TestFrameworkAnnotations.MAPPING.annotationName}("${endpoint.path}")""")
    }

}
