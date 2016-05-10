//package pp.block3.cc.test;
//
//import org.antlr.v4.runtime.*;
//import org.antlr.v4.runtime.tree.ParseTree;
//import org.antlr.v4.runtime.tree.ParseTreeWalker;
//import org.junit.Test;
//import pp.block3.cc.antlr.*;
//
//import static org.junit.Assert.assertEquals;
//
///**
// * Created by Jens on 9-5-2016.
// *
// */
//public class TTest {
//
//    private final ParseTreeWalker walker = new ParseTreeWalker();
//    private final TeaListener teaListener = new TeaListener();
//
//
//    @Test
//    public void test() {
//        test(Type.BOOL, "True+False");
//        test(Type.ERR, "True^2");
//        test(Type.NUM, "2+2");
//        test(Type.STR, "a^2");
//        test(Type.NUM, "5+(4+(8-3))");
//    }
//
//    private void test(Type expected, String text) {
//        assertEquals(expected, parseTAttr(text).type);
//        ParseTree tree = parseT(text);
//        this.teaListener.init();
//        this.walker.walk(this.teaListener, tree);
//
//        assertEquals(expected, this.teaListener.getTypes(tree));
//    }
//
//    private ParseTree parseT(String text) {
//        CharStream chars = new ANTLRInputStream(text);
//        Lexer lexer = new TLexer(chars);
//        TokenStream tokens = new CommonTokenStream(lexer);
//        TParser parser = new TParser(tokens);
//        return parser.t();
//    }
//
//
//    private TAttrParser.TContext parseTAttr(String text) {
//        CharStream chars = new ANTLRInputStream(text);
//        Lexer lexer = new TAttrLexer(chars);
//        TokenStream tokens = new CommonTokenStream(lexer);
//        TAttrParser parser = new TAttrParser(tokens);
//        return parser.t();
//    }
//}
