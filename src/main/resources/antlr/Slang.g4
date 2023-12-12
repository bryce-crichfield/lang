grammar Slang;
@header{
package lang.antlr.generated;
}
// Compilation Unit ====================================================================================================
compilationUnit
    :   header body
    ;
header
    :   moduleDeclaration (importDeclaration)*
    ;
body
    :  (interfaceDeclaration | structDeclaration | functionDeclaration | variable)*
    ;
// DECLARATION  ========================================================================================================
moduleDeclaration
    :   'module' ID
    ;
importDeclaration
    :   'import' ID
    ;
interfaceDeclaration
    :   'interface' ID interfaceExtendsList?'{' interfaceMethodList '}'
    ;
interfaceExtendsList
    :   ':' ID (',' ID)*
    ;
interfaceMethodList
    :   (interfaceMethodDeclaration (',' interfaceMethodDeclaration)*)?
    ;
interfaceMethodDeclaration
    :   ID '(' functionParameterList ')' type statementBlock?
    ;
functionDeclaration
    :   ID '(' functionParameterList ')' type statementBlock
    ;
functionParameterList
    :   (functionParameter (',' functionParameter)*)?
    ;
functionParameter
    :   ID ':' type
    ;
structDeclaration
    :   'struct' ID structImplementsList? '{' (structFieldDeclaration | structMethodDeclaration)* '}'
    ;
structImplementsList
    :   ':' ID (',' ID)*
    ;
structFieldDeclaration
    :   ID ':' type structFieldInitializer? ';'
    ;
structFieldInitializer
    :   '=' expression
    ;
structMethodDeclaration
    :   'overrides'? ID '(' functionParameterList ')' type statementBlock
    ;
variable
    :   variableDeclaration '=' expression
    |   variableAssignment '=' expression
    ;
variableDeclaration
    :   'var' ID ':' type
    |   'var' ID
    ;
variableAssignment
    :   accessor
    ;
// STATEMENT ===========================================================================================================
statementBlock
    :   '{' statement* '}'
    ;
statement
    :   variable ';'
    |   statementIf
    |   statementWhile
    |   statementFor
    |   statementReturn ';'
    |   statementBreak ';'
    |   statementContinue ';'
    |   statementDefer ';'
    |   statementBlock
    |   expression ';'
    ;
statementIf
    :   'if' expression statementBlock ('else if' expression statementBlock)* ('else' statementBlock)?
    ;
statementWhile
    :   'while' expression statementBlock
    ;
statementFor
    :   'for' ID '...' expression statementBlock
    ;
statementReturn
    :   'return' expression?
    ;
statementBreak
    :   'break'
    ;
statementContinue
    :   'continue'
    ;
statementDefer
    :   'defer' expression
    |   'defer' statementBlock
    ;
// EXPRESSION ==========================================================================================================
expression
    :   expression EXPR_OPERATOR term
    |   structInitializer
    |   arrayConstructor
    |   application
    |   term
    ;
arrayConstructor
    :   typeArray '(' (expression (',' expression)*)? ')'
    ;
application
    :   accessor '(' (expression (',' expression)*)? ')'
    ;
structInitializer
    :   ID '{' structInitializerField* '}'
    ;
structInitializerField
    :   ID ':' expression ';'
    ;
term
    :   term TERM_OPERATOR factor
    |   factor
    ;
factor
    :   operand POSTFIX
    |   UNARY operand
    |   operand
    |   '(' expression ')'
    ;
tuple
    :   '(' (expression (',' expression)*)? ')'
    ;
operand
    :   NUMBER
    |   STRING
    |   NULL
    |   accessor
    |   tuple
    ;
accessor
    :   ID ('.' ID)*
    ;
// Types ===============================================================================================================
type
    :   ID
    |   typeArray
    |   typeTuple
    |   typeUnion
    ;
typeArray
    :   '[]' type
    ;
typeTuple
    :   '(' (type (',' type)*)? ')'
    ;
typeUnion
    :   '(' (type ('|' type)*)? ')'
    ;
// OPERATOR ============================================================================================================
ASSIGN: '=' | '+=' | '-=' | '*=' | '/=' ;
EXPR_OPERATOR: '+' | '-' | '==' | '!=' | '<' | '<=' | '>' | '>=' | '&&' | '||' ;
TERM_OPERATOR: '*' | '/' | '%' ;
UNARY: '~' | '-' ;
POSTFIX: '++' | '--' ;
// TERMINAL ============================================================================================================
ID: [a-zA-Z]+;
NUMBER: [0-9]+('.'[0-9]+)?;
STRING: '"' .*? '"';
NULL: 'null';
WHITE_SPACE: [ \t\r\n]+ -> skip;
SINGLE_COMMENT: '//' .*? '\n' -> skip;
MULTI_COMMENT: '/*' .*? '*/' -> skip;