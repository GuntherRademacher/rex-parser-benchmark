                                                            lexer grammar JSONLexer;

                                                            @header {
                                                              package de.bottlecaps.rex.benchmark.json.antlr4;
                                                            }

                                                            @members {
                                                              private String unhex(String value) {
                                                                return Character.valueOf((char) Integer.parseInt(value, 16)).toString();
                                                              }
                                                            }
BEGIN_STRING
           : '"' -> pushMode(StringMode)
           ;
DOUBLE     : INT FRAC EXP?
           | INT FRAC? EXP
           ;
INT        : '-'? ( DIGIT | ONENINE DIGITS )
           ;
fragment
DIGITS     : DIGIT+
           ;
fragment
DIGIT      : '0'
           | ONENINE
           ;
fragment
ONENINE    : [1-9]
           ;
fragment
FRAC       : '.' DIGITS
           ;
fragment
EXP        : ( 'E' | 'e' ) SIGN DIGITS
           ;
fragment
SIGN       : ( '+' | '-' )?
           ;
WS         : [\u{0009}\u{000A}\u{000D}\u{0020}]+ -> skip
           ;
TRUE       : 'true'
           ;
FALSE      : 'false'
           ;
NULL       : 'null'
           ;
LBRACE     : '{'
           ;
COMMA      : ','
           ;
RBRACE     : '}'
           ;
COLON      : ':'
           ;
LBRACKET   : '['
           ;
RBRACKET   : ']'
           ;

mode StringMode;

CHARACTERS
           : [\u{0020}-\u{0021}\u{0023}-\u{005B}\u{005D}-\u{10FFFF}]+
           ;
ESCAPE     : '\\"'                                          {setText("\"");}
           | '\\\\'                                         {setText("\\");}
           | '\\/'                                          {setText("/");}
           | '\\b'                                          {setText("\b");}
           | '\\f'                                          {setText("\f");}
           | '\\n'                                          {setText("\n");}
           | '\\r'                                          {setText("\r");}
           | '\\t'                                          {setText("\t");}
           | '\\u' HEX HEX HEX HEX                          {setText(unhex(getText().substring(2)));}
           ;
END_STRING : '"' -> popMode
           ;
fragment
HEX        : [0-9A-Fa-f]
           ;
