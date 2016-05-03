package pp.block2.cc;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Token;
import pp.block2.cc.ll.LQR;

import java.util.List;

/**
 * Created by Jens on 2-5-2016.
 *
 */
public class LQRParser implements Parser {

    private SymbolFactory fact;
    private List<? extends Token> tokens;
    private int index;

    private static final NonTerm L = new NonTerm("L");
    private static final NonTerm R = new NonTerm("R");
    private static final NonTerm Q = new NonTerm("Q");
    private static final NonTerm Ra = new NonTerm("Ra");
    private static final NonTerm H = new NonTerm("H");

    public LQRParser() {
        this.fact = new SymbolFactory(LQR.class);
    }

    @Override
    public AST parse(Lexer lexer) throws ParseException {
        this.tokens = lexer.getAllTokens();
        this.index = 0;
        return parseLQR();
    }

    private AST parseLQR() throws ParseException {
        return parseL();
    }

    private AST parseL() throws ParseException {
        AST result = new AST(L);
        Token next = peek();

        switch (next.getType()) {
            case LQR.A:
                result.addChild(parseR());
                result.addChild(parseA());
                break;
            case LQR.B:
                result.addChild(parseQ());
                result.addChild(parseB());
                result.addChild(parseA());
                break;
            case LQR.C:
                result.addChild(parseR());
                result.addChild(parseA());
                break;
            default:
                System.out.println(next.getType());
                System.out.println(next.getText());
                throw unparsable(L);
        }
        return result;
    }

    private AST parseR() throws ParseException {
        AST result = new AST(R);
        Token next = peek();

        switch (next.getType()) {
            case LQR.A:
                result.addChild(parseA());
                result.addChild(parseB());
                result.addChild(parseA());
                result.addChild(parseRa());
                break;
            case LQR.C:
                result.addChild(parseC());
                result.addChild(parseA());
                result.addChild(parseB());
                result.addChild(parseA());
                result.addChild(parseRa());
                break;
            default:
                throw unparsable(R);
        }
        return result;
    }

    private AST parseRa() throws ParseException {
        AST result = new AST(Ra);
        Token next = peek();

        switch (next.getType()) {
            case LQR.B:
                result.addChild(parseB());
                result.addChild(parseC());
                result.addChild(parseRa());
                break;
            case LQR.A:
                break;
            default:
                throw unparsable(Ra);
        }
        return result;
    }

    private AST parseQ() throws ParseException {
        AST result = new AST(Q);
        Token next = peek();

        switch (next.getType()) {
            case LQR.B:
                result.addChild(parseB());
                result.addChild(parseH());
                break;
            default:
                throw unparsable(Q);
        }
        return result;
    }

    private AST parseH() throws ParseException {
        AST result = new AST(H);
        Token next = peek();

        switch (next.getType()) {
            case LQR.B:
                result.addChild(parseB());
                result.addChild(parseC());
                break;
            case LQR.C:
                result.addChild(parseC());
                break;
            default:
                throw unparsable(H);
        }
        return result;
    }

    private AST parseA() throws ParseException {
        Token next = next();
        if (next.getType() != LQR.A) {
            throw new ParseException(String.format(
                    "Line %d:%d - expected token '%s' but found '%s'",
                    next.getLine(), next.getCharPositionInLine(),
                    this.fact.get(LQR.A), this.fact.get(next.getType())));
        }
        return new AST(this.fact.getTerminal(LQR.A), next);
    }

    private AST parseB() throws ParseException {
        Token next = next();
        if (next.getType() != LQR.B) {
            throw new ParseException(String.format(
                    "Line %d:%d - expected token '%s' but found '%s'",
                    next.getLine(), next.getCharPositionInLine(),
                    this.fact.get(LQR.B), this.fact.get(next.getType())));
        }
        return new AST(this.fact.getTerminal(LQR.B), next);
    }

    private AST parseC() throws ParseException {
        Token next = next();
        if (next.getType() != LQR.C) {
            throw new ParseException(String.format(
                    "Line %d:%d - expected token '%s' but found '%s'",
                    next.getLine(), next.getCharPositionInLine(),
                    this.fact.get(LQR.C), this.fact.get(next.getType())));
        }
        return new AST(this.fact.getTerminal(LQR.C), next);
    }

    private Token next() throws ParseException {
        Token result = peek();
        index++;
        return result;
    }
    private Token peek() throws ParseException {
        if (this.index >= this.tokens.size()) {
            throw new ParseException("Reading beyond end of input");
        }
        return this.tokens.get(this.index);
    }
    private ParseException unparsable(NonTerm nt) {
        try {
            Token next = peek();
            return new ParseException(String.format(
                    "Line %d:%d - could not parse '%s' at token '%s'",
                    next.getLine(), next.getCharPositionInLine(), nt.getName(),
                    this.fact.get(next.getType())));
        } catch (ParseException e) {
            return e;
        }
    }
    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Usage: [text]+");
        } else {
            for (String text : args) {
                CharStream stream = new ANTLRInputStream(text);
                Lexer lexer = new LQR(stream);
                try {
                    System.out.printf("Parse tree: %n%s%n",
                            new LQRParser().parse(lexer));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
