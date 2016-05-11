package pp.block3.cc.symbol;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;

/**
 * Created by Jens on 10-5-2016.
 *
 */
public class SymbolListener implements DeclUseListener {

    private ParseTreeProperty<String> errors;

    public SymbolListener() {
        this.errors = new ParseTreeProperty<>();
    }

    @Override
    public void enterProgram(DeclUseParser.ProgramContext ctx) {

    }

    @Override
    public void exitProgram(DeclUseParser.ProgramContext ctx) {

    }

    @Override
    public void enterSeries(DeclUseParser.SeriesContext ctx) {

    }

    @Override
    public void exitSeries(DeclUseParser.SeriesContext ctx) {

    }

    @Override
    public void enterUnit(DeclUseParser.UnitContext ctx) {

    }

    @Override
    public void exitUnit(DeclUseParser.UnitContext ctx) {

    }

    @Override
    public void enterDecl(DeclUseParser.DeclContext ctx) {

    }

    @Override
    public void exitDecl(DeclUseParser.DeclContext ctx) {

    }

    @Override
    public void enterUse(DeclUseParser.UseContext ctx) {

    }

    @Override
    public void exitUse(DeclUseParser.UseContext ctx) {

    }

    @Override
    public void visitTerminal(TerminalNode terminalNode) {

    }

    @Override
    public void visitErrorNode(ErrorNode errorNode) {
        String errorMessage = "Error on line: " + errorNode.getSymbol().getLine() + " at position: "
                + errorNode.getSymbol().getCharPositionInLine();
        setError(errorNode, errorMessage);
    }

    @Override
    public void enterEveryRule(ParserRuleContext parserRuleContext) {

    }

    @Override
    public void exitEveryRule(ParserRuleContext parserRuleContext) {

    }

    public ArrayList<String> getErrors() {
        ArrayList<String> result = new ArrayList<>();
        return result;
    }

    private void setError(ParseTree node, String errorMessage) {
        this.errors.put(node, errorMessage);
    }
}
