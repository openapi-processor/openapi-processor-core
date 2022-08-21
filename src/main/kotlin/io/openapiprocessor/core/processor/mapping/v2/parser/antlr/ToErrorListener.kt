/*
 * Copyright © 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.processor.mapping.v2.parser.antlr

import org.antlr.v4.runtime.BaseErrorListener
import org.antlr.v4.runtime.RecognitionException
import org.antlr.v4.runtime.Recognizer

/**
 * throw ToException on error
 */
class ToErrorListener: BaseErrorListener() {

    override fun syntaxError(recognizer: Recognizer<*, *>?, offendingSymbol: Any?, line: Int,pos: Int, msg: String, e: RecognitionException) {
        throw ToException(line, pos, msg, e)
    }

}
