/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java;

import java.io.StringWriter
import java.io.Writer

class AnnotationWriter {

    fun write (target: Writer, annotation: Annotation) {
        target.write("@${annotation.typeName}")

        val parameters = mutableListOf<String>()
        annotation.parameters.forEach {
            if (it.key == "") {
                parameters.add(it.value)
            } else {
                parameters.add("${it.key} = ${it.value}")
            }
        }

        if (parameters.isNotEmpty()) {
            target.write("(")
            target.write(parameters.joinToString(", "))
            target.write(")")
        }
    }
}

fun buildAnnotation(annotation: Annotation): String {
    val writer = StringWriter()
    AnnotationWriter().write(writer, annotation)
    return writer.toString()
}
