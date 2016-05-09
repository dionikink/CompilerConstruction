grammar T;

import TVocab;

t : t HAT t     #hat
  | t PLUS t    #plus
  | t EQUALS t  #equals
  | LPAR t RPAR #par
  | NUM         #number
  | BOOL        #bool
  | STR         #str
  ;