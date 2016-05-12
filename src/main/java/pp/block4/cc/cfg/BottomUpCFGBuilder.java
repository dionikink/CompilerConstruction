package pp.block4.cc.cfg;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;
import pp.block4.cc.ErrorListener;
import pp.block4.cc.cfg.FragmentParser.BlockStatContext;
import pp.block4.cc.cfg.FragmentParser.BreakStatContext;

/** Template bottom-up CFG builder. */
public class BottomUpCFGBuilder extends FragmentBaseListener {
	/** The CFG being built. */
	private Graph graph;
	private ParseTreeProperty<Node> beginNodes;
	private ParseTreeProperty<Node> endNodes;

	/** Builds the CFG for a program contained in a given file. */
	public Graph build(File file) {
		Graph result = null;
		ErrorListener listener = new ErrorListener();
		try {
			CharStream chars = new ANTLRInputStream(new FileReader(file));
			Lexer lexer = new FragmentLexer(chars);
			lexer.removeErrorListeners();
			lexer.addErrorListener(listener);
			TokenStream tokens = new CommonTokenStream(lexer);
			FragmentParser parser = new FragmentParser(tokens);
			parser.removeErrorListeners();
			parser.addErrorListener(listener);
			ParseTree tree = parser.program();
			if (listener.hasErrors()) {
				System.out.printf("Parse errors in %s:%n", file.getPath());
				for (String error : listener.getErrors()) {
					System.err.println(error);
				}
			} else {
				result = build(tree);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override public void exitProgram(FragmentParser.ProgramContext ctx) {
		for (int i = 0; i < ctx.stat().size() - 1; i++) {
			getEndNode(ctx.stat(i)).addEdge(getBeginNode(ctx.stat(i+1)));
		}
	}

	@Override public void exitPrintStat(FragmentParser.PrintStatContext ctx) {
		Node print = addNode(ctx, "Print");
		addBeginNode(ctx, print);
		addEndNode(ctx, print);
	}

	@Override public void exitBlockStat(FragmentParser.BlockStatContext ctx) {
		int last = ctx.stat().size() - 1;
		addBeginNode(ctx, getBeginNode(ctx.stat(0)));
		addEndNode(ctx, getEndNode(ctx.stat(last)));
		for (int i = 0; i < ctx.stat().size() - 1; i++) {
			System.out.println(ctx.stat(i).getText());
			getEndNode(ctx.stat(i)).addEdge(getBeginNode(ctx.stat(i+1)));
		}
	}

	@Override public void exitWhileStat(FragmentParser.WhileStatContext ctx) {
		Node cond = addNode(ctx, "While " + ctx.expr().getText());
		cond.addEdge(getBeginNode(ctx.stat()));
		getEndNode(ctx.stat()).addEdge(cond);
		addBeginNode(ctx, cond);
		addEndNode(ctx, cond);
	}

	@Override public void exitIfStat(FragmentParser.IfStatContext ctx) {
		Node iff = addNode(ctx, "If " + ctx.expr().getText());
		iff.addEdge(getBeginNode(ctx.stat(0)));
		Node endif = addNode(ctx, "EndIf");
		if (ctx.stat().size() > 1) {
			iff.addEdge(getBeginNode(ctx.stat(1)));
			getEndNode(ctx.stat(1)).addEdge(endif);
			addEndNode(ctx, endif);
		} else {
			getEndNode(ctx.stat(0)).addEdge(endif);
			addEndNode(ctx, endif);
		}
		addBeginNode(ctx, iff);
	}

	@Override public void exitAssignStat(FragmentParser.AssignStatContext ctx) {
		Node node = addNode(ctx, "Assign " + ctx.target().getText() + " to " + ctx.expr().getText());
		addBeginNode(ctx, node);
		addEndNode(ctx, node);
	}

	@Override public void exitDecl(FragmentParser.DeclContext ctx) {
		Node node = addNode(ctx, "Declare " + ctx.type().getText());
		addBeginNode(ctx, node);
		addEndNode(ctx, node);
	}

	/** Builds the CFG for a program given as an ANTLR parse tree. */
	public Graph build(ParseTree tree) {
		this.graph = new Graph();
		this.beginNodes = new ParseTreeProperty<>();
		this.endNodes = new ParseTreeProperty<>();
		ParseTreeWalker walker = new ParseTreeWalker();
		walker.walk(this, tree);
		return graph;
	}



	@Override
	public void enterBreakStat(BreakStatContext ctx) {
		throw new IllegalArgumentException("Break not supported");
	}

	@Override
	public void enterContStat(FragmentParser.ContStatContext ctx) {
		throw new IllegalArgumentException("Continue not supported");
	}

	/** Adds a node to he CGF, based on a given parse tree node.
	 * Gives the CFG node a meaningful ID, consisting of line number and 
	 * a further indicator.
	 */
	private Node addNode(ParserRuleContext node, String text) {
		return this.graph.addNode(node.getStart().getLine() + ": " + text);
	}

	private void addEndNode(ParseTree tree, Node node) {
		this.endNodes.put(tree, node);
	}

	private Node getEndNode(ParseTree tree) {
		return this.endNodes.get(tree);
	}

	private void addBeginNode(ParseTree tree, Node node) {
		this.beginNodes.put(tree, node);
	}

	private Node getBeginNode(ParseTree tree) {
		return this.beginNodes.get(tree);
	}

	/** Main method to build and print the CFG of a simple Java program. */
	public static void main(String[] args) {
		if (args.length == 0) {
			System.err.println("Usage: [filename]+");
			return;
		}
		BottomUpCFGBuilder builder = new BottomUpCFGBuilder();
		for (String filename : args) {
			File file = new File(filename);
			System.out.println(filename);
			System.out.println(builder.build(file));
		}
	}
}
