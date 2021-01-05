/*
 * Copyright © 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer

/**
 * create javadoc from OpenAPI descriptions.
 */
class JavaDocWriter {

    fun convert(description: String): String {
        val parser = Parser
            .builder()
            .build()

        val doc = parser.parse(description)
        val renderer = HtmlRenderer
            .builder()
            .build()

        var javadoc = "/**\n"

        javadoc += renderer
            .render(doc)
            .dropLastWhile { it == '\n' }
            .lineSequence()
            .map {
                " * $it"
            }.joinToString("\n")

        javadoc += "\n */\n"
        return javadoc
    }

}
