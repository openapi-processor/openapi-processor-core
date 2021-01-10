/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

class FormattingException(
    private val source: String,
    cause: Throwable
): java.lang.RuntimeException(cause) {

    override val message: String
        get() = "failed to format the generated source: \n>>\n$source\n<<"

}
