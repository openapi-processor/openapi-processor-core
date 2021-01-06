/*
 * Copyright © 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.openapiprocessor.core.builder.api.endpoint
import io.openapiprocessor.core.model.datatypes.StringDataType
import io.openapiprocessor.core.model.parameters.ParameterBase

class JavaDocWriterSpec: StringSpec({

    lateinit var writer: JavaDocWriter

    beforeTest {
        writer = JavaDocWriter()
    }

    "converts endpoint description to javadoc comment" {
        val endpointDescription = """
            *markdown* description with **text**
        
            - one list item
            - second list item
        
            ```
            code block
            ```

            """.trimIndent()

        val endpoint = endpoint("/foo") {
            description(endpointDescription)
            responses {
                status("204") {
                    response()
                }
            }
        }

        val html = writer.convert(endpoint, endpoint.endpointResponses.first())

        html shouldBe """
            /**
             * <em>markdown</em> description with <strong>text</strong>
             * <ul>
             * <li>one list item</li>
             * <li>second list item</li>
             * </ul>
             * <pre><code>code block
             * </code></pre>
             */

            """.trimIndent()
    }

    "converts endpoint parameter description to javadoc @param" {
        val parameterDescription = """
            *markdown* description with **text**
        
            - one list item
            - second list item
        
            ```
            code block
            ```

            """.trimIndent()

        val endpoint = endpoint("/foo") {
            description("any")
            parameters {
                any(object : ParameterBase("foo", StringDataType(),
                    true, false, parameterDescription) {})
            }
            responses {
                status("204") {
                    response()
                }
            }
        }

        val html = writer.convert(endpoint, endpoint.endpointResponses.first())

        html shouldBe """
            /**
             * any
             *
             * @param foo <em>markdown</em> description with <strong>text</strong>
             * <ul>
             * <li>one list item</li>
             * <li>second list item</li>
             * </ul>
             * <pre><code>code block
             * </code></pre>
             */

            """.trimIndent()
    }

})
