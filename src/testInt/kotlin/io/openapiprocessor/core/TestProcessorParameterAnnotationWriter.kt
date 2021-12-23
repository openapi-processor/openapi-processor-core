package io.openapiprocessor.core

import io.openapiprocessor.core.model.parameters.Parameter
import io.openapiprocessor.core.writer.java.ParameterAnnotationWriter
import java.io.Writer

/**
 * simple [io.openapiprocessor.core.writer.java.ParameterAnnotationWriter] implementation for testing.
 */
class TestProcessorParameterAnnotationWriter: ParameterAnnotationWriter {
    override fun write(target: Writer, parameter: Parameter) {
        target.write (PARAMETER.annotationName)
    }
}
