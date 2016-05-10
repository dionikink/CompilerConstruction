package pp.block3.cc.test;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.junit.Assert;
import org.junit.Test;
import pp.block3.cc.antlr.*;

import static org.junit.Assert.assertEquals;

/**
 * Created by Dion on 9-5-2016.
 */
public class TTest {

    private final T t = new T();

    @Test
    public void testT() {
        test(Type.STR, "\"test\"");
        test(Type.BOOL, "True + False");
        test(Type.ERR, "False + 1");
        test(Type.STR, "3 + \"test\"");
        test(Type.NUM, "3 + 2");
        test(Type.ERR, "True ^ 3");
        test(Type.NUM, "3^3");
        test(Type.BOOL, "True == True");
        test(Type.NUM, "(5)");
    }

    private void test(Type expected, String expr) {
        Assert.assertEquals(expected, parseTAttr(expr).val);
        ParseTree tree = parseT(expr);
        (new ParseTreeWalker()).walk(this.t, tree);
        assertEquals(expected, this.t.type(tree));
    }

    private ParseTree parseT(String text) {
        CharStream chars = new ANTLRInputStream(text);
        Lexer lexer = new TLexer(chars);
        TokenStream tokens = new CommonTokenStream(lexer);
        TParser parser = new TParser(tokens);
        return parser.t();
    }

    private TAttrParser.TContext parseTAttr(String text) {
        CharStream chars = new ANTLRInputStream(text);
        Lexer lexer = new TAttrLexer(chars);
        TokenStream tokens = new CommonTokenStream(lexer);
        TAttrParser parser = new TAttrParser(tokens);
        return parser.t();
    }
}
