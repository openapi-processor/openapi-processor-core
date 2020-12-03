/*
 * Copyright © 2019 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.support

import io.openapiprocessor.core.model.Endpoint
import io.openapiprocessor.core.model.EndpointResponse
import io.openapiprocessor.core.writer.java.MappingAnnotationWriter
import java.io.Writer

class TestMappingAnnotationWriter: MappingAnnotationWriter {

    override fun write(target: Writer, endpoint: Endpoint, endpointResponse: EndpointResponse) {
        target.write ("@CoreMapping")
    }

}
