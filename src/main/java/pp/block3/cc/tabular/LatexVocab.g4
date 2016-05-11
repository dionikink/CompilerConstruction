lexer grammar LatexVocab;

BEGIN : '\\begin{tabular}' ;
END   : '\\end{tabular}' ;
ARGS : ('l' | 'c' | 'r')+ ;

CONTENT : ( ~('\\' | '\n' | '{' |'}' | '$' | '&' | '#' | '^' | '_' | '~'| '%') | '' )+;

NEWLINE  : '\r\n' ;

LBRACKET : '{' ;
RBRACKET : '}' ;
SEP      : '&' ;
EOL      : '\\\\' ;

COMMENT : ('%'  ~('\r'|'\n')* ('\r'|'\n')+) -> skip;

