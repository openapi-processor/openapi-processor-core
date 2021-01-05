/*
 * Copyright © 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class JavaDocWriterSpec: StringSpec({

    lateinit var writer: JavaDocWriter

    beforeTest {
        writer = JavaDocWriter()
    }

    "converts markdown to javadoc comment"  {

        val md = """
            *markdown* description with **text**
        
            - one list item
            - second list item
        
            ```
            code block
            ```

            """.trimIndent()

        val html = writer.convert(md)

        html shouldBe """
            /**
             * <p><em>markdown</em> description with <strong>text</strong></p>
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
