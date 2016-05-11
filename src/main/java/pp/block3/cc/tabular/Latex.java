package pp.block3.cc.tabular;

import org.antlr.v4.runtime.tree.TerminalNode;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

/**
 * Created by Dion on 11-5-2016.
 *
 */
public class Latex extends LatexBaseListener {

    private PrintWriter writer;

    public void init(String fileName) {
        try {
            this.writer = new PrintWriter(fileName + ".html", "UTF-8");
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void end() {
        this.writer.close();
    }

    @Override public void exitTable(LatexParser.TableContext ctx) {

    }

    @Override public void exitArguments(LatexParser.ArgumentsContext ctx) {

    }

    @Override public void exitTablerow(LatexParser.TablerowContext ctx) {

    }

    @Override public void exitRowcontent(LatexParser.RowcontentContext ctx) {

    }

    @Override public void visitTerminal(TerminalNode node) {

    }
}

