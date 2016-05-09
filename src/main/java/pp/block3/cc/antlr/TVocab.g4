lexer grammar TVocab;

HAT    : '^' ;
PLUS   : '+' ;
EQUALS : '==' ;
LPAR   : '(' ;
RPAR   : ')' ;

NUM  : [0-9]+;
BOOL : 'True' | 'False' ;
STR  : '"' ('\\' ["\\] | ~["\\\r\n])* '"';

// ignore whitespace
WS : [ \t\n\r] -> skip;

