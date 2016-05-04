package pp.block2.cc.antlr;

import org.antlr.v4.runtime.*;

import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;
import pp.block2.cc.AST;
import pp.block2.cc.ParseException;
import pp.block2.cc.Parser;
import pp.block2.cc.SymbolFactory;
import pp.block2.cc.ll.*;
import pp.block2.cc.ll.SentenceLexer;

public class SentenceConverter //
		extends SentenceBaseListener implements Parser {
	/** Factory needed to create terminals of the {@link Sentence}
	 * grammar. See {@link pp.block2.cc.ll.SentenceParser} for
	 * example usage. */
	private final SymbolFactory fact;

    public static void main(String[] args) {
        CharStream charStream = new ANTLRInputStream(args[0]);
        Lexer lexer = new SentenceLexer(charStream);

        SentenceConverter sentenceConverter = new SentenceConverter();
        AST ast = null;

        try {
            ast = sentenceConverter.parse(lexer);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

	public SentenceConverter() {
		this.fact = new SymbolFactory(Sentence.class);
	}

	@Override
	public AST parse(Lexer lexer) throws ParseException {
        TokenStream tokens = new CommonTokenStream(lexer);
        // Build a parser instance on top of the token stream
        SentenceParser parser = new SentenceParser(tokens);
        // Get the parse tree by calling the start rule
        ParseTree tree = parser.sentence();
        // Print the (formatted) parse tree
        System.out.println(tree.toStringTree(parser));
        walk(tree);
        return null;
	}

    public void walk(ParseTree tree) {
        new ParseTreeWalker().walk(this, tree);
    }

	@Override
	public void enterSentence(SentenceParser.SentenceContext ctx) {
        System.out.println("enterSentence");
    }

	@Override
	public void exitSentence(SentenceParser.SentenceContext ctx) {
        System.out.println("exitSentence");
    }

	@Override
	public void enterSubject(SentenceParser.SubjectContext ctx) {
        System.out.println("enterSubject");
    }

	@Override
	public void exitSubject(SentenceParser.SubjectContext ctx) {
        System.out.println("exitSubject");
    }

	@Override
	public void enterObject(SentenceParser.ObjectContext ctx) { }

	@Override
	public void exitObject(SentenceParser.ObjectContext ctx) { }

	@Override
	public void enterModifier(SentenceParser.ModifierContext ctx) { }

	@Override
	public void exitModifier(SentenceParser.ModifierContext ctx) { }

	@Override
	public void enterEveryRule(ParserRuleContext ctx) {}

	@Override
	public void exitEveryRule(ParserRuleContext ctx) {}

	@Override
	public void visitTerminal(TerminalNode node) { }

	@Override
	public void visitErrorNode(ErrorNode node) { }

}
