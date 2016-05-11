package pp.block3.cc.test;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.junit.Assert;
import org.junit.Test;
import pp.block3.cc.antlr.CalcAttrLexer;
import pp.block3.cc.antlr.CalcAttrParser;
import pp.block3.cc.antlr.CalcLexer;
import pp.block3.cc.antlr.CalcParser;
import pp.block3.cc.symbol.DeclUseLexer;
import pp.block3.cc.symbol.DeclUseParser;
import pp.block3.cc.symbol.SymbolListener;

import java.lang.reflect.Array;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

/**
 * Created by Dion on 11-5-2016.
 *
 */
public class SymbolListenerTest {

    private ParseTreeWalker walker = new ParseTreeWalker();
    private SymbolListener sListener = new SymbolListener();

    @Test
    public void test() {
        test(0, "D: aap (U: aap D: noot D: aap (U: noot ) (D: noot U: noot )) U: aap");
        test(1, "(D: aap)");
    }

    private void test(int expected, String expr) {
        ParseTree tree = parseSymbol(expr);
        this.walker.walk(this.sListener, tree);
        System.out.println(this.sListener.getErrors(tree).size());
        if (this.sListener.getErrors(tree).size() == expected) {
            for (String s : this.sListener.getErrors(tree)) {
                System.out.println(s);
            }
        } else {
            Assert.fail();
        }
    }

    private ParseTree parseSymbol(String text) {
        CharStream chars = new ANTLRInputStream(text);
        Lexer lexer = new DeclUseLexer(chars);
        TokenStream tokens = new CommonTokenStream(lexer);
        DeclUseParser parser = new DeclUseParser(tokens);
        return parser.decl();
    }
}
