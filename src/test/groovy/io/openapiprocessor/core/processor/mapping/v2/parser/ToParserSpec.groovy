/*
 * Copyright © 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.processor.mapping.v2.parser

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.tree.ParseTreeWalker
import spock.lang.Specification
import spock.lang.Unroll

class ToParserSpec extends Specification {

    @Unroll
    void "parses add mapping without annotation '#source'"() {
        when:
       	def lexer = new ToLexer(CharStreams.fromString(source))
        def tokens = new CommonTokenStream(lexer)
       	def parser = new ToParser(tokens)
        def r = parser.to ()

        def extractor = new ToExtractor ()
        new ParseTreeWalker().walk (extractor, r)

        then:
        def target = extractor.target
        target.type == type
        target.typeArguments == typeArguments

        where:
        source                            | type                      | typeArguments
        'Foo'                             | 'Foo'                     | []
        'io.openapiprocessor.Foo'         | 'io.openapiprocessor.Foo' | []
        '  io . openapiprocessor .  Foo ' | 'io.openapiprocessor.Foo' | []
        'A<>'                             | 'A'                       | []
        'A<?>'                            | 'A'                       | []
        'A<b.B, c.C>'                     | 'A'                       | ['b.B', 'c.C']
        'A<B, C>'                         | 'A'                       | ['B', 'C']
    }

    @Unroll
    void "parses add mapping with annotation '#source'"() {
        when:
       	def lexer = new ToLexer(CharStreams.fromString(source))
        def tokens = new CommonTokenStream(lexer)
       	def parser = new ToParser(tokens)
        def r = parser.to ()

        def extractor = new ToExtractor ()
        new ParseTreeWalker().walk (extractor, r)

        then:
        def target = extractor.target
        target.annotationType == type
        target.annotationParameters == typeArguments

        where:
        source                                  | type    | typeArguments
        'Foo A'                                 | 'Foo'   | null
        'f.Foo A'                               | 'f.Foo' | null
        '   Foo    A'                           | 'Foo'   | null
        'Foo (   ) A'                           | 'Foo'   | '(   )'
        'Foo (  "val"   ) A'                    | 'Foo'   | '(  "val"   )'
        'Foo ( value = "val"  ,  bar = 42  ) A' | 'Foo'   | '( value = "val"  ,  bar = 42  )'
    }


    void "throws on lex and parse error"() {
        when:
       	def lexer = new ToLexer(CharStreams.fromString(source))
        lexer.removeErrorListeners()
        lexer.addErrorListener(new ToErrorListener())

        def tokens = new CommonTokenStream(lexer)
       	def parser = new ToParser(tokens)
        parser.removeErrorListeners()
        parser.addErrorListener(new ToErrorListener())

        def r = parser.to ()

        def extractor = new ToExtractor ()
        new ParseTreeWalker().walk (extractor, r)

        then:
        def e = thrown (ToException)

        where:
        source                                  | type   | typeArguments
        ''                                      | null   | null
    }

}
