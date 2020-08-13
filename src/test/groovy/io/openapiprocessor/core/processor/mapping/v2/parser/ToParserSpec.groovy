package io.openapiprocessor.core.processor.mapping.v2.parser

import org.antlr.v4.runtime.BaseErrorListener
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.RecognitionException
import org.antlr.v4.runtime.Recognizer
import org.antlr.v4.runtime.misc.ParseCancellationException
import org.antlr.v4.runtime.tree.ParseTreeWalker
import spock.lang.Specification
import spock.lang.Unroll

class ToParserSpec extends Specification {

    class Data {
        String type
        List<String> typeArguments = []

        String annotationType
        String annotationArguments
    }

    class ToExtractor extends ToBaseListener {
        Data target

        @Override
        void enterAnnotationType (ToParser.AnnotationTypeContext ctx) {
            // type string with package
            target.annotationType = ctx.type ().text
            target.annotationArguments = ctx.AnnotationAnyArguments ()?.text
        }

        @Override
        void exitToType (ToParser.ToTypeContext ctx) {
            // type string "{pkg.}Type"
            target.type = ctx.type ().text

            // type strings of <> type arguments
            ctx.typeArguments ()
                ?.typeArgumentList ()
                ?.typeArgument ()
                // skip if empty
                ?.findAll {
                    it.type () != null
                }
                ?.each {
                    target.typeArguments.add (it.type ().text)
                }
        }

    }

    class ToException extends ParseCancellationException {
        int line
        int pos

        ToException (int line, int pos, String msg, RecognitionException e) {
            super(msg, e)
            this.line = line
            this.pos = pos
        }
    }

    class ToErrorListener extends BaseErrorListener {
        @Override
        void syntaxError (Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int pos, String msg, RecognitionException e) {
            throw new ToException(line, pos, msg, e)
        }
    }

    @Unroll
    void "parses add mapping without annotation '#source'"() {
        when:
       	def lexer = new ToLexer(CharStreams.fromString(source))
        def tokens = new CommonTokenStream(lexer)
       	def parser = new ToParser(tokens)
        def r = parser.to ()

        Data result = new Data()
        def listener = new ToExtractor (target: result)
        new ParseTreeWalker().walk (listener, r)

        then:
        result.type == type
        result.typeArguments == typeArguments

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

        Data result = new Data()
        def listener = new ToExtractor (target: result)
        new ParseTreeWalker().walk (listener, r)

        then:
        result.annotationType == type
        result.annotationArguments == typeArguments

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

        Data result = new Data()
        def listener = new ToExtractor (target: result)
        new ParseTreeWalker().walk (listener, r)

        then:
        def e = thrown (ToException)

        where:
        source                                  | type   | typeArguments
        ''                                      | null   | null
    }

}
