/*
 * Copyright © 2019 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.support

import io.openapiprocessor.core.model.parameters.Parameter
import io.openapiprocessor.core.writer.java.ParameterAnnotationWriter
import java.io.Writer

class TestParameterAnnotationWriter: ParameterAnnotationWriter {

    override fun write(target: Writer, parameter: Parameter) {
        target.write ("@Parameter")
    }

}
