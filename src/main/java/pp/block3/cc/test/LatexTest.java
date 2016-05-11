package pp.block3.cc.test;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.Test;
import pp.block2.cc.Parser;
import pp.block3.cc.tabular.LatexLexer;
import pp.block3.cc.tabular.LatexParser;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Dion on 11-5-2016.
 */
public class LatexTest {

    @Test
    public void test() {
        System.out.println(parseLatex("tabular-2.tex"));
    }

    private ParseTree parseLatex(String fileName) {
        FileReader file;
        CharStream chars = null;

        try {
            file = new FileReader(fileName);
            chars = new ANTLRInputStream(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Lexer lexer = new LatexLexer(chars);
        TokenStream tokens = new CommonTokenStream(lexer);
        LatexParser parser = new LatexParser(tokens);
        return parser.table();
    }

}
