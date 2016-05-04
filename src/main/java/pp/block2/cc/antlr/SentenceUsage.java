package pp.block2.cc.antlr;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

public class SentenceUsage {
	public static void main(String[] args) {
		parse("all smart undergraduate students love all compilers.");
		parse("all students love love.");

		walk(parse(args[0]));
	}

	public static ParseTree parse(String text) {
		// Convert the input text to a character stream
		CharStream stream = new ANTLRInputStream(text);
		// Build a lexer on top of the character stream
		Lexer lexer = new SentenceLexer(stream);
		// Extract a token stream from the lexer
		TokenStream tokens = new CommonTokenStream(lexer);
		// Build a parser instance on top of the token stream
		SentenceParser parser = new SentenceParser(tokens);
		// Get the parse tree by calling the start rule
		ParseTree tree = parser.sentence();
		// Print the (formatted) parse tree
		System.out.println(tree.toStringTree(parser));

		return tree;
	}

	public static void walk(ParseTree tree) {
		new ParseTreeWalker().walk(new SentenceConverter(), tree);
	}
}
