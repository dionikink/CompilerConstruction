grammar Latex;

import LatexVocab;

table      : BEGIN arguments NEWLINE (tablerow)* END ;
arguments  : LBRACKET ARGS RBRACKET ;
tablerow   : rowcontent ('&' rowcontent)* EOL NEWLINE;
rowcontent : CONTENT
           |
           ;





