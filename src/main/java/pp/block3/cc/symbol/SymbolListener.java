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
public class SymbolListener extends DeclUseBaseListener {

    private ParseTreeProperty<String> errors;

    public SymbolListener() {
        this.errors = new ParseTreeProperty<>();
    }

    @Override
    public void visitErrorNode(ErrorNode errorNode) {
        String errorMessage = "Error on line: " + errorNode.getSymbol().getLine() + " at position: "
                + errorNode.getSymbol().getCharPositionInLine();
        setError(errorNode, errorMessage);
    }

    public ArrayList<String> getErrors(ParseTree root) {
        ArrayList<String> result = new ArrayList<>();
        for (int i = 0; i < root.getChildCount(); i++) {
            if (errors.get(root.getChild(i)) != null) {
                result.add(errors.get(root.getChild(i)));
            }
        }
        return result;
    }

    private void setError(ParseTree node, String errorMessage) {
        this.errors.put(node, errorMessage);
    }
}
