grammar Mapping;

/*
 * Parser Rules
 */


// root:
mapping
    : type | map | annotate | content
    ;

type
    : plainType | targetType
    ;

map
    : sourceType Arrow targetType
    ;

content
    : contentType Arrow targetType
    ;

annotate
    : sourceType Annotate annotationType
    ;

plainType
    : Plain
    ;

sourceType
    : sourceIdentifier (':' formatIdentifier)?
    ;

targetType
    : (annotationType)? qualifiedTargetType
    ;

annotationType
    : qualifiedType ('(' (annotationParameters)? ')')?
    ;

contentType
    : ContentType
    ;

annotationParameters
    : annotationParameterUnnamed | annotationParameterNamed (',' annotationParameterNamed)*
    ;

annotationParameterUnnamed
    : (Identifier | Boolean | String | Number)
    ;

annotationParameterNamed
    : Identifier '=' (Identifier | Boolean | String | Number)
    ;

qualifiedTargetType
    : qualifiedType
    ;

qualifiedType
    : QualifiedType ('<' genericParameters '>')?
    ;

genericParameters
    : genericParameter (',' genericParameter)*
    ;

genericParameter
    : QualifiedType
    ;

sourceIdentifier
    :  Identifier | String
    ;

formatIdentifier
    : Identifier | Format | String
    ;


/*
 * Lexer Rules
 */

Arrow: '=>';
Annotate: '@';

Plain: 'plain';
Boolean: 'true' | 'false';
Package: '{package-name}';

DoubleQuote: '"';

Whitespace
  : [ \t] -> skip
  ;

Identifier
  : JavaLetter JavaLetterOrDigit*
  ;

QualifiedType
    : (Identifier | Package) ('.' Identifier)*
    ;


Format
    : FormatLetter FormatLetterOrDigit*
    ;

String
    : DoubleQuote ( ~["\\] | '\\' [\t\\"] )* DoubleQuote
    ;

MimeType
    : [a-zA-Z_] ([a-zA-Z0-9\\._-])*
    ;

MimeSubType
    : [a-zA-Z_] ([a-zA-Z0-9\\._-])*
    ;

ContentType
    : MimeType '/' MimeSubType
    ;

// "any" number format (we only want to split the parameters list)
Number
    : ([a-fA-FlLxX0-9\\._])+
    ;

fragment JavaLetter
    : [a-zA-Z$_]
    ;

fragment JavaLetterOrDigit
    : [a-zA-Z0-9$_]
    ;

fragment FormatLetter
    :  [a-zA-Z_-]
    ;

fragment FormatLetterOrDigit
    : [a-zA-Z0-9_-]
    ;
