/*
 * Copyright © 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import io.openapiprocessor.core.model.Endpoint
import io.openapiprocessor.core.model.EndpointResponse
import org.commonmark.node.Document
import org.commonmark.node.Node
import org.commonmark.node.Paragraph
import org.commonmark.parser.Parser
import org.commonmark.renderer.NodeRenderer
import org.commonmark.renderer.html.CoreHtmlNodeRenderer
import org.commonmark.renderer.html.HtmlNodeRendererContext
import org.commonmark.renderer.html.HtmlRenderer

/**
 * Do not wrap the top level items in (unwanted) paragraphs.
 */
class SkipParentWrapperParagraphsRenderer(val context: HtmlNodeRendererContext)
    : CoreHtmlNodeRenderer(context), NodeRenderer {

    override fun getNodeTypes(): Set<Class<out Node>> {
        return setOf(Paragraph::class.java)
    }

    override fun render(node: Node) {
        if (node.parent is Document) {
            visitChildren(node)
        } else {
            visit(node as Paragraph)
        }
    }
}

/**
 * create javadoc from OpenAPI descriptions.
 */
open class JavaDocWriter {

    val parser: Parser = Parser
        .builder()
        .build()

    val renderer: HtmlRenderer = HtmlRenderer
        .builder()
        .nodeRendererFactory { context -> SkipParentWrapperParagraphsRenderer(context) }
        .build()

    fun convert(endpoint: Endpoint, endpointResponse: EndpointResponse): String {
        var javadoc = "/**\n"

        javadoc += convert(endpoint.description)
        javadoc += "\n"

        if (endpoint.parameters.isNotEmpty())
            javadoc += " *\n"

        endpoint.parameters.forEach {
            javadoc += convert(it.description, "@param ${it.name}")
            javadoc += "\n"
        }

        javadoc += " */\n"
        return javadoc
    }

    private fun convert(description: String?, intro: String? = null): String {
        if (description.isNullOrEmpty())
            return ""

        val doc = parser.parse(description)

        var result = ""
        if (intro != null) {
            result = intro
            result += " "
        }

        result += renderer
            .render(doc)

        return result
            .dropLastWhile { it == '\n' }
            .lineSequence()
            .map {
                " * $it"
            }.joinToString("\n")
    }

}
