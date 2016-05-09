package pp.block2.cc.antlr;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import pp.block2.cc.*;
import pp.block2.cc.Parser;

import java.math.BigInteger;

/**
 * Created by Dion on 9-5-2016.
 */
public class Calculator extends ArithmeticBaseListener implements Parser {

    private ParseTreeProperty<AST> node;
    private final SymbolFactory fact;
    private ParseTreeProperty<BigInteger> evalTree;
    private ParseTreeProperty<String> operators;

    public Calculator() {
        this.node = new ParseTreeProperty<>();
        this.fact = new SymbolFactory(ArithmeticLexer.class);
        this.evalTree = new ParseTreeProperty<>();
        this.operators = new ParseTreeProperty<>();
    }

    public BigInteger compute(String expression) {
        ParseTree tree = null;

        try {
            tree = parseToTree(new ArithmeticLexer(new ANTLRInputStream(expression)));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return getEval(tree);
    }

    @Override
    public AST parse(Lexer lexer) throws ParseException {
        this.node = new ParseTreeProperty<>();
        // Extract a token stream from the lexer
        TokenStream tokens = new CommonTokenStream(lexer);
        // Build a parser instance on top of the token stream
        ArithmeticParser parser = new ArithmeticParser(tokens);
        // Get the parse tree by calling the start rule
        ParseTree tree = parser.expr();
        walk(tree);
        // Print the (formatted) parse tree
        return getASTNode(tree);
    }

    public ParseTree parseToTree(Lexer lexer) throws ParseException {
        this.node = new ParseTreeProperty<>();
        // Extract a token stream from the lexer
        TokenStream tokens = new CommonTokenStream(lexer);
        // Build a parser instance on top of the token stream
        ArithmeticParser parser = new ArithmeticParser(tokens);
        // Get the parse tree by calling the start rule
        ParseTree tree = parser.expr();
        walk(tree);
        // Print the (formatted) parse tree
        return tree;
    }

    public void walk(ParseTree tree) {
        new ParseTreeWalker().walk(this, tree);
    }

    private AST getASTNode(ParseTree node) {
        return this.node.get(node);
    }

    private void setNode(ParseTree node, AST astNode) {
        this.node.put(node, astNode);
    }

    private BigInteger getEval(ParseTree node) {
        return this.evalTree.get(node);
    }

    private void setEvalTree(ParseTree node, BigInteger value) {
        this.evalTree.put(node, value);
    }

    @Override
    public void exitExprPower(ArithmeticParser.ExprPowerContext ctx) {
        AST sent = new AST(new NonTerm("Power"));

        for (int i = 0; i < ctx.getChildCount(); i++) {
            sent.addChild(getASTNode(ctx.getChild(i)));
        }

        setNode(ctx, sent);
        setEvalTree(ctx, (getEval(ctx.expr(0)).pow(getEval(ctx.expr(1)).intValue())));
    }

    @Override
    public void exitExprMult(ArithmeticParser.ExprMultContext ctx) {
        AST sent = new AST(new NonTerm("Mult"));

        for (int i = 0; i < ctx.getChildCount(); i++) {
            sent.addChild(getASTNode(ctx.getChild(i)));
        }

        setNode(ctx, sent);
        setEvalTree(ctx, (getEval(ctx.expr(0)).multiply(getEval(ctx.expr(1)))));
    }

    @Override
    public void exitExprPlus(ArithmeticParser.ExprPlusContext ctx) {
        AST sent = new AST(new NonTerm("Plus"));

        for (int i = 0; i < ctx.getChildCount(); i++) {
            sent.addChild(getASTNode(ctx.getChild(i)));
        }

        setNode(ctx, sent);
        setEvalTree(ctx, (getEval(ctx.expr(0)).add(getEval(ctx.expr(1)))));
    }

    @Override
    public void exitExprMinus(ArithmeticParser.ExprMinusContext ctx) {
        AST sent = new AST(new NonTerm("Minus"));

        for (int i = 0; i < ctx.getChildCount(); i++) {
            sent.addChild(getASTNode(ctx.getChild(i)));
        }

        setNode(ctx, sent);
        setEvalTree(ctx, (getEval(ctx.expr(0)).subtract(getEval(ctx.expr(1)))));
    }

    @Override
    public void exitExprNegation(ArithmeticParser.ExprNegationContext ctx) {
        AST sent = new AST(new NonTerm("Negation"));

        for (int i = 0; i < ctx.getChildCount(); i++) {
            sent.addChild(getASTNode(ctx.getChild(i)));
        }

        setNode(ctx, sent);
        setEvalTree(ctx, getEval(ctx.expr()).negate());
    }

    @Override
    public void exitExprParens(ArithmeticParser.ExprParensContext ctx) {
        AST sent = new AST(new NonTerm("Parens"));

        for (int i = 0; i < ctx.getChildCount(); i++) {
            sent.addChild(getASTNode(ctx.getChild(i)));
        }

        setNode(ctx, sent);
        setEvalTree(ctx, getEval(ctx.expr()));
    }

    @Override
    public void exitExprConstant(ArithmeticParser.ExprConstantContext ctx) {
        AST sent = new AST(new NonTerm("Constant"));

        for (int i = 0; i < ctx.getChildCount(); i++) {
            sent.addChild(getASTNode(ctx.getChild(i)));
        }

        setNode(ctx, sent);
        setEvalTree(ctx, getEval(ctx.NUM()));
    }

    @Override
    public void visitTerminal(TerminalNode node) {
        Term terminal = fact.getTerminal(node.getSymbol().getType());
        AST term = new AST(terminal, node.getSymbol());
        setNode(node, term);
        if (terminal.getName().equals("NUM")) {
            BigInteger i = new BigInteger(node.getSymbol().getText());
            setEvalTree(node, i);
        }
    }



    public static void main(String[] args) {
        Calculator calculator = new Calculator();
        BigInteger result = calculator.compute("(5*10) + (21 - (5^2))");
        System.out.println(result);
    }

}
