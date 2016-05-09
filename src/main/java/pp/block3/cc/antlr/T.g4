grammar T;

import TVocab;

t : t HAT t     #tHat
  | t PLUS t    #tPlus
  | t EQUALS t  #tEquals
  | LPAR t RPAR #tParens
  | NUM         #tNum
  | BOOL        #tBool
  | STR         #tStr
  ;


