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
fun parseMapping(type: String): Mapping {
    val lexer = MappingLexer(CharStreams.fromString(type))
    val tokens = CommonTokenStream(lexer)
    val parser = MappingParser(tokens)
    val ctx = parser.mapping()
    val extractor = MappingExtractor()
    ParseTreeWalker().walk(extractor, ctx)
    return extractor//.mapping
}

/**
 * parse "to" grammar
 */
private fun parseTo(type: String): ToData {
    val lexer = ToLexer(CharStreams.fromString(type))
    val tokens = CommonTokenStream(lexer)
    val parser = ToParser(tokens)
    val ctx = parser.to()
    val extractor = ToExtractor()
    ParseTreeWalker().walk(extractor, ctx)
    return extractor.getTarget()
}
