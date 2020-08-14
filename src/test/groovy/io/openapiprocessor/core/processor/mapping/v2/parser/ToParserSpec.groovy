/*
 * Copyright 2020 the original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
        extractor.target.type == type
        extractor.target.typeArguments == typeArguments

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

        ToData target = new ToData()
        def listener = new ToExtractor (target)
        new ParseTreeWalker().walk (listener, r)

        then:
        target.annotationType == type
        target.annotationArguments == typeArguments

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

        ToData target = new ToData()
        def listener = new ToExtractor (target: target)
        new ParseTreeWalker().walk (listener, r)

        then:
        def e = thrown (ToException)

        where:
        source                                  | type   | typeArguments
        ''                                      | null   | null
    }

}
