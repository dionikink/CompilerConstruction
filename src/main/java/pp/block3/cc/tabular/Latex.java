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

    public static void main(String[] args) {

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
        writer.println(ctx.CONTENT().getText());
    }

    @Override public void exitRowcontent(LatexParser.RowcontentContext ctx) {
        writer.println("</td>");
    }

    @Override public void visitTerminal(TerminalNode node) {

    }
}

