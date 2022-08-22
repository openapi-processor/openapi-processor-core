/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.processor.mapping.v2.parser.antlr

import org.antlr.v4.runtime.RecognitionException

class MappingParserException(val line: Int, val pos: Int, msg: String, e: RecognitionException)
    : Exception(msg, e)

