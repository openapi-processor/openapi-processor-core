grammar To;

/*
 * Parser Rules
 */

to
  : annotationType? toType
  ;

annotationType
  : type AnnotationAnyArguments?
  ;

toType
  : type typeArguments?
  ;

type
  : packageName '.' typeName
  | typeName
  ;

packageName
  : Identifier
  | packageName '.' Identifier
  ;

typeName
  : Identifier
  ;

typeArguments
  : '<' typeArgumentList '>'
  ;

typeArgumentList
  : typeArgument (',' typeArgument)*
  ;

typeArgument
  : type
  | '?'
  | .*?
  ;


/*
 * Lexer Rules
 */


fragment JavaLetter
  : [a-zA-Z$_]
  ;

fragment JavaLetterOrDigit
  : [a-zA-Z0-9$_]
  ;

AnnotationAnyArguments
  : '(' .*? ')'
  ;

Identifier
  : JavaLetter JavaLetterOrDigit*
  ;

Whitespace
  : ' ' -> skip
  ;

