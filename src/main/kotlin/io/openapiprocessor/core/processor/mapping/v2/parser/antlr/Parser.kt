/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.processor.mapping.v2.parser.antlr

import io.openapiprocessor.core.processor.mapping.v2.parser.Mapping
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.tree.ParseTreeWalker

/**
 * parse "mapping" grammar
 */
fun parseMapping(mapping: String): Mapping {
    try {
        val lexer = MappingLexer(CharStreams.fromString(mapping))
        val tokens = CommonTokenStream(lexer)
        val parser = MappingParser(tokens)
        parser.addErrorListener(MappingErrorListener())
        val ctx = parser.mapping()
        val extractor = MappingExtractor()
        ParseTreeWalker().walk(extractor, ctx)
        return extractor
    } catch (e: MappingParserException) {
        throw MappingException(mapping, e)
    }
}
