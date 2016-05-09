grammar Arithmetic;

expr
    : <assoc=right> expr POWER expr     #ExprPower
    |               expr MULT expr      #ExprMult
    |               expr PLUS expr      #ExprPlus
    |               expr MINUS expr     #ExprMinus
    |               MINUS expr          #ExprNegation
    |               LPAR expr RPAR      #ExprParens
    |               NUM                 #ExprConstant
    ;

MULT : '*' ;
PLUS : '+' ;
MINUS : '-' ;
POWER : '^' ;

LPAR : '(' ;
RPAR : ')' ;

NUM : ('0'..'9')+ ;

WS : [ \t\n\r] -> skip ;
