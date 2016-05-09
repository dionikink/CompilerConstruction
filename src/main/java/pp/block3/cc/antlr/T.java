package pp.block3.cc.antlr;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;

/**
 * Created by Dion on 9-5-2016.
 */
public class T extends TBaseListener {
    private ParseTreeProperty<Type> types;

    public T() {
        this.types = new ParseTreeProperty<Type>();
    }

    @Override
    public void exitTHat(TParser.THatContext ctx) {
        if (type(ctx.t(0)) == Type.NUM && type(ctx.t(1)) == Type.NUM){
            set(ctx, Type.NUM);
        } else if (type(ctx.t(0)) == Type.STR && type(ctx.t(1)) == Type.NUM){
            set(ctx, Type.STR);
        } else {
            set(ctx, Type.ERR);
        }

    }

    @Override
    public void exitTPlus(TParser.TPlusContext ctx) {
        if (type(ctx.t(0)) == Type.NUM && type(ctx.t(1)) == Type.NUM){
            set(ctx, Type.NUM);
        } else if (type(ctx.t(0)) == Type.BOOL && type(ctx.t(1)) == Type.BOOL){
            set(ctx, Type.BOOL);
        } else if (type(ctx.t(0)) == Type.STR || type(ctx.t(1)) == Type.STR){
            set(ctx, Type.STR);
        } else {
            set(ctx, Type.ERR);
        }
    }

    @Override
    public void exitTEquals(TParser.TEqualsContext ctx) {
        if (type(ctx.t(0)) == type(ctx.t(1))){
            set(ctx, Type.BOOL);
        } else {
            set(ctx, Type.ERR);
        }
    }

    @Override
    public void exitTParens(TParser.TParensContext ctx) {
        set(ctx, type(ctx.t()));
    }

    @Override
    public void exitTBool(TParser.TBoolContext ctx) {
        set(ctx, Type.BOOL);
    }

    @Override
    public void exitTStr(TParser.TStrContext ctx) {
        set(ctx, Type.STR);
    }

    @Override
    public void exitTNum(TParser.TNumContext ctx) {
        set(ctx, Type.NUM);
    }

    private void set(ParseTree node, Type type) {
        this.types.put(node, type);
    }

    public Type type(ParseTree node) {
        return this.types.get(node);
    }
}
