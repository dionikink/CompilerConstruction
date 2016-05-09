grammar TAttr;

import TVocab;

@members {
    private boolean isInt(String text) {
        try {
            Integer.parseInt(text);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isBoolean(String text) {
        if (text.equals("True") || text.equals("False")) {
            return true;
        }
        return false;
    }

    private Type getPlus(String t0, String t1) {
        if (isBoolean(t0) && isBoolean(t1)) {
            return Type.BOOL;
        } else if (isInt(t0) && isInt(t1)) {
            return Type.NUM;
        } else if (isInt(t0) || isInt(t1) || isBoolean(t0) || isBoolean(t1)) {
            return Type.ERR;
        } else {
            return Type.STR;
        }
    }

    private Type getHat(String t0, String t1) {
        if (isBoolean(t0) || isBoolean(t1)) {
            return Type.ERR;
         } else if (isInt(t1)) {
            if (isInt(t0)) {
                return Type.NUM;
            } else {
                return Type.STR;
            }
         } else {
            return Type.ERR;
         }
    }

    private Type getEquals(String t0, String t1) {
        if (isBoolean(t0) && isBoolean(t1)) {
            return Type.BOOL;
        } else if (isInt(t0) && isInt(t1)) {
            return Type.NUM;
        } else if (isInt(t0) || isInt(t1) || isBoolean(t0) || isBoolean(t1)) {
            return Type.ERR;
        } else {
            return Type.STR;
        }
    }
}

t returns [ Type type ]
    : t0=t HAT t1=t
    { $type = getHat($t0.text, $t1.text); }
    | t0=t PLUS t1=t
    { $type = getPlus($t0.text, $t1.text); }
    | t0=t EQUALS t1=t
    { $type = getEquals($t0.text, $t1.text); }
    | LPAR t0=t RPAR
    { $type = $t0.type; }
    | NUM
    { $type = Type.NUM; }
    | BOOL
    { $type = Type.BOOL; }
    | STR
    { $type = Type.STR; }
    ;