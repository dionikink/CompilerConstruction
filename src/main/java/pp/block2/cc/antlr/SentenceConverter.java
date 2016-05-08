package pp.block2.cc.antlr;

import org.antlr.v4.runtime.*;

import org.antlr.v4.runtime.tree.*;
import pp.block2.cc.*;
import pp.block2.cc.Parser;
import pp.block2.cc.ll.Sentence;

public class SentenceConverter //
		extends SentenceBaseListener implements Parser {
	/** Factory needed to create terminals of the {@link Sentence}
	 * grammar. See {@link pp.block2.cc.ll.SentenceParser} for
	 * example usage. */
	private final SymbolFactory fact;
	private ParseTreeProperty<AST> node;
	private boolean error = false;

    public static void main(String[] args) {
        CharStream charStream = new ANTLRInputStream("students all undergraduate love all compilers");
        Lexer lexer = new SentenceLexer(charStream);

        SentenceConverter sentenceConverter = new SentenceConverter();
        AST ast = null;

        try {
            ast = sentenceConverter.parse(lexer);
        } catch (ParseException e) {
            e.printStackTrace();
        }
		System.out.println(ast);
	}

	public SentenceConverter() {
		this.fact = new SymbolFactory(Sentence.class);
		this.node = new ParseTreeProperty<>();
	}

	@Override
	public AST parse(Lexer lexer) throws ParseException {
		this.node = new ParseTreeProperty<AST>();
		// Extract a token stream from the lexer
		TokenStream tokens = new CommonTokenStream(lexer);
		// Build a parser instance on top of the token stream
		SentenceParser parser = new SentenceParser(tokens);
		// Get the parse tree by calling the start rule
		ParseTree tree = parser.sentence();
		walk(tree);
		// Print the (formatted) parse tree
		if (error) {
			throw (new ParseException("Failed to parse properly."));
		}
		return getASTNode(tree);
	}

	private void setError() {
		this.error = true;
	}

	private AST getASTNode(ParseTree node) {
		return this.node.get(node);
	}

	private void setNode(ParseTree node, AST astNode) {
		this.node.put(node, astNode);
	}
	public void walk(ParseTree tree) {
		new ParseTreeWalker().walk(this, tree);
	}

	// From here on overwrite the listener methods
	// Use an appropriate ParseTreeProperty to
	// store the correspondence from nodes to ASTs
	@Override public void exitSentence(SentenceParser.SentenceContext ctx) {
		AST sent = new AST(pp.block2.cc.ll.SentenceParser.getSent());
		for (int i = 0; i < ctx.getChildCount(); i++) {
			sent.addChild(getASTNode(ctx.getChild(i)));
		}
		setNode(ctx, sent);
	}

	@Override public void exitSubject(SentenceParser.SubjectContext ctx) {
		AST subj = new AST(pp.block2.cc.ll.SentenceParser.getSubj());
		for (int i = 0; i < ctx.getChildCount(); i++) {
			subj.addChild(getASTNode(ctx.getChild(i)));
		}
		setNode(ctx, subj);
	}
	@Override public void exitObject(SentenceParser.ObjectContext ctx) {
		AST obj = new AST(pp.block2.cc.ll.SentenceParser.getObj());
		for (int i = 0; i < ctx.getChildCount(); i++) {
			obj.addChild(getASTNode(ctx.getChild(i)));
		}
		setNode(ctx, obj);
	}
	@Override public void exitModifier(SentenceParser.ModifierContext ctx) {
		AST mod = new AST(pp.block2.cc.ll.SentenceParser.getMod());
		for (int i = 0; i < ctx.getChildCount(); i++) {
			mod.addChild(getASTNode(ctx.getChild(i)));
		}
		setNode(ctx, mod);
	}
	@Override public void exitEveryRule(ParserRuleContext ctx) {
	}
	@Override public void visitTerminal(TerminalNode node) {
		AST term = new AST(fact.getTerminal(node.getSymbol().getType()), node.getSymbol());
		setNode(node, term);
	}
	@Override public void visitErrorNode(ErrorNode node) {
		setError();
	}

}
