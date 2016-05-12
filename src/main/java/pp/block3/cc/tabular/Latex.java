package pp.block3.cc.tabular;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.io.*;

/**
 * Created by Dion on 11-5-2016.
 *
 */
public class Latex extends LatexBaseListener {

    private PrintWriter writer;

    public static void main(String[] args) {
        Latex latex = new Latex();
        FileReader file;
        CharStream chars = null;

        try {
            file = new FileReader(args[0]);
            chars = new ANTLRInputStream(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Lexer lexer = new LatexLexer(chars);
        latex.parse(lexer, args[0]);
    }

    public void parse(Lexer lexer, String fileName){
        LatexParser parser = new LatexParser(new CommonTokenStream(lexer));
        LatexErrorListener errorListener = new LatexErrorListener();
        parser.removeErrorListeners();
        parser.addErrorListener(errorListener);
        ParseTree tree = parser.table();

        if (errorListener.getErrors().size() > 0){
            System.out.println(errorListener.getErrors().toString());
        } else {
            this.init(fileName);
            new ParseTreeWalker().walk(this, tree);
            this.end();
        }
    }

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

    @Override
    public void enterTable(LatexParser.TableContext ctx) {
        writer.println("<html>");
        writer.println("<body>");
        writer.println("<table border = \"1\">");
    }

    @Override public void exitTable(LatexParser.TableContext ctx) {
        writer.println("</table>");
        writer.println("</body>");
        writer.println("</html>");
    }

    @Override public void exitArguments(LatexParser.ArgumentsContext ctx) {

    }

    @Override public void exitTablerow(LatexParser.TablerowContext ctx) {
        writer.println("</tr>");
    }

    @Override
    public void enterTablerow(LatexParser.TablerowContext ctx) {
        writer.println("<tr>");
    }

    @Override
    public void enterRowcontent(LatexParser.RowcontentContext ctx) {
        writer.println("<td>");
        if(ctx.CONTENT() != null) {
            writer.println(ctx.CONTENT().getText());
        } else {
            writer.println();
        }
    }

    @Override public void exitRowcontent(LatexParser.RowcontentContext ctx) {
        writer.println("</td>");
    }

    @Override public void visitTerminal(TerminalNode node) {

    }
}

