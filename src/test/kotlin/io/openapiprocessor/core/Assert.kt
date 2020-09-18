/*
 * Copyright © 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core

import java.io.StringWriter

fun extractImports(source: StringWriter): List<String> {
    return source.toString()
        .lines()
        .filter { it.startsWith("import") }
        .toList()
}

fun extractBody(source: StringWriter):  List<String> {
    val body = source.toString()

    val start = body.indexOf("{\n")
    val end = body.indexOf("}\n")

    return body.substring(start + 2, end - 1)
        .lines()
}
