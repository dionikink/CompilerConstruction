grammar TAttr;

import TVocab;

@members {
    private Type getHatType(Type t1, Type t2) {
		if (t1 == Type.NUM && t2 == Type.NUM) {
			return Type.NUM;
		} else if (t1 == Type.STR && t2 == Type.NUM) {
			return Type.STR;
		} else {
			return Type.ERR;
		}
	}

	private Type getPlusType(Type t1, Type t2) {
        if (t1 == Type.NUM && t2 == Type.NUM) {
            return Type.NUM;
        } else if (t1 == Type.BOOL && t2 == Type.BOOL) {
            return Type.BOOL;
        }  else if (t1 == Type.STR || t2 == Type.STR) {
            return Type.STR;
        } else {
            return Type.ERR;
        }
    }

    private Type getEqualsType(Type t1, Type t2) {
        if (t1 == t2) {
            return Type.BOOL;
        } else {
            return Type.ERR;
        }
    }
}

t returns [ Type val ]
    : t0=t HAT t1=t
      { $val = getHatType($t0.val, $t1.val); }
    | t0=t PLUS t1=t
      { $val = getPlusType($t0.val, $t1.val); }
    | t0=t EQUALS t1=t
      { $val = getEqualsType($t0.val, $t1.val); }
    | LPAR t0=t RPAR
      { $val = $t0.val; }
    | NUM
      { $val = Type.NUM; }
    | BOOL
      { $val = Type.BOOL; }
    | STR
      { $val = Type.STR; }
    ;