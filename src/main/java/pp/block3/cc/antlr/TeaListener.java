//package pp.block3.cc.antlr;
//
//import org.antlr.v4.runtime.ParserRuleContext;
//import org.antlr.v4.runtime.tree.ErrorNode;
//import org.antlr.v4.runtime.tree.ParseTree;
//import org.antlr.v4.runtime.tree.ParseTreeProperty;
//import org.antlr.v4.runtime.tree.TerminalNode;
//
///**
// * Created by Jens on 9-5-2016.
// *
// */
//public class TeaListener implements TListener {
//
//    private ParseTreeProperty<Type> types;
//
//    public void init() {
//        this.types = new ParseTreeProperty<>();
//    }
//
//    private void setTypes(ParseTree node, Type t) {
//        this.types.put(node, t);
//    }
//
//    private boolean isInt(String text) {
//        try {
//            Integer.parseInt(text);
//            return true;
//        } catch (NumberFormatException e) {
//            return false;
//        }
//    }
//
//    private boolean isBoolean(String text) {
//        if (text.equals("True") || text.equals("False")) {
//            return true;
//        }
//        return false;
//    }
//
//    private Type getPlus(String t0, String t1) {
//        if (isBoolean(t0) && isBoolean(t1)) {
//            return Type.BOOL;
//        } else if (isInt(t0) && isInt(t1)) {
//            return Type.NUM;
//        } else if (isInt(t0) || isInt(t1) || isBoolean(t0) || isBoolean(t1)) {
//            return Type.ERR;
//        } else {
//            return Type.STR;
//        }
//    }
//
//    private Type getHat(String t0, String t1) {
//        if (isBoolean(t0) || isBoolean(t1)) {
//            return Type.ERR;
//        } else if (isInt(t1)) {
//            if (isInt(t0)) {
//                return Type.NUM;
//            } else {
//                return Type.STR;
//            }
//        } else {
//            return Type.ERR;
//        }
//    }
//
//    private Type getEquals(String t0, String t1) {
//        if (isBoolean(t0) && isBoolean(t1)) {
//            return Type.BOOL;
//        } else if (isInt(t0) && isInt(t1)) {
//            return Type.NUM;
//        } else if (isInt(t0) || isInt(t1) || isBoolean(t0) || isBoolean(t1)) {
//            return Type.ERR;
//        } else {
//            return Type.STR;
//        }
//    }
//
//    public Type getTypes(ParseTree node) {
//        return this.types.get(node);
//    }
//
//    @Override
//    public void visitTerminal(TerminalNode terminalNode) {
//        if (isBoolean(terminalNode.getText())) {
//            setTypes(terminalNode, Type.BOOL);
//        } else if (isInt(terminalNode.getText())) {
//            setTypes(terminalNode, Type.NUM);
//        } else {
//            setTypes(terminalNode, Type.STR);
//        }
//    }
//
//    @Override
//    public void visitErrorNode(ErrorNode errorNode) {
//
//    }
//
//    @Override
//    public void enterEveryRule(ParserRuleContext parserRuleContext) {
//
//    }
//
//    @Override
//    public void exitEveryRule(ParserRuleContext parserRuleContext) {
//
//    }
//
//    @Override
//    public void enterPar(TParser.ParContext ctx) {
//
//    }
//
//    @Override
//    public void exitPar(TParser.ParContext ctx) {
//        if (isBoolean(ctx.t().getText())) {
//            setTypes(ctx, Type.BOOL);
//        } else if (isInt(ctx.t().getText())) {
//            setTypes(ctx, Type.NUM);
//        } else {
//            setTypes(ctx, Type.STR);
//        }
//    }
//
//    @Override
//    public void enterStr(TParser.StrContext ctx) {
//
//    }
//
//    @Override
//    public void exitStr(TParser.StrContext ctx) {
//        setTypes(ctx, Type.STR);
//    }
//
//    @Override
//    public void enterNumber(TParser.NumberContext ctx) {
//
//    }
//
//    @Override
//    public void exitNumber(TParser.NumberContext ctx) {
//        setTypes(ctx, Type.NUM);
//    }
//
//    @Override
//    public void enterBool(TParser.BoolContext ctx) {
//
//    }
//
//    @Override
//    public void exitBool(TParser.BoolContext ctx) {
//        setTypes(ctx, Type.BOOL);
//    }
//
//    @Override
//    public void enterEquals(TParser.EqualsContext ctx) {
//
//    }
//
//    @Override
//    public void exitEquals(TParser.EqualsContext ctx) {
//        setTypes(ctx, getEquals(ctx.t(0).getText(), ctx.t(1).getText()));
//    }
//
//    @Override
//    public void enterHat(TParser.HatContext ctx) {
//
//    }
//
//    @Override
//    public void exitHat(TParser.HatContext ctx) {
//        setTypes(ctx, getHat(ctx.t(0).getText(), ctx.t(1).getText()));
//    }
//
//    @Override
//    public void enterPlus(TParser.PlusContext ctx) {
//
//    }
//
//    @Override
//    public void exitPlus(TParser.PlusContext ctx) {
//        setTypes(ctx, getPlus(ctx.t(0).getText(), ctx.t(1).getText()));
//    }
//}
