package pp.block4.cc.cfg;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import pp.block4.cc.ErrorListener;
import pp.block4.cc.cfg.FragmentParser.BreakStatContext;
import pp.block4.cc.cfg.FragmentParser.ContStatContext;
import pp.block4.cc.cfg.FragmentParser.ProgramContext;

/** Template top-down CFG builder. */
public class TopDownCFGBuilder extends FragmentBaseListener {
	/** The CFG being built. */
	private Graph graph;

	private ParseTreeProperty<ParseTree> appendTo;
	private ParseTreeProperty<ParseTree> appendIf;
	private ParseTreeProperty<ParseTree> appendWhile;
	private ParseTreeProperty<Node> nodes;
	private ParseTreeProperty<Node> ifNodes;
	private ParseTreeProperty<Node> whileNode;

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
			ProgramContext tree = parser.program();
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

	/** Builds the CFG for a program given as an ANTLR parse tree. */
	public Graph build(ProgramContext tree) {
		this.graph = new Graph();
		this.appendTo = new ParseTreeProperty<>();
		this.nodes = new ParseTreeProperty<>();
		this.ifNodes = new ParseTreeProperty<>();
		this.appendIf = new ParseTreeProperty<>();
		this.whileNode = new ParseTreeProperty<>();
		this.appendWhile = new ParseTreeProperty<>();
        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(this, tree);
        return graph;
	}

	@Override public void enterProgram(ProgramContext ctx) {
		for (int i = 0; i < ctx.stat().size() -1; i++) {
			addToAppendList(ctx.stat(i+1), ctx.stat(i));
		}
	}

	@Override public void enterDecl(FragmentParser.DeclContext ctx) {
		Node node = addNode(ctx, "Declare " + ctx.type().getText());
		if (getAppendIf(ctx) != null) {
			getIfNode(getAppendIf(ctx)).addEdge(node);
			node.addEdge(getNode(getAppendIf(ctx)));
		}
		if (getAppendList(ctx) != null) {
			getNode(getAppendList(ctx)).addEdge(node);
		}
		if (getAppendWhile(ctx) != null) {
			node.addEdge(getWhileNode(getAppendWhile(ctx)));
		}
		setNode(ctx, node);
	}

	@Override public void enterIfStat(FragmentParser.IfStatContext ctx) {
		Node iff = addNode(ctx, "If " + ctx.expr().getText());
		Node endif = addNode(ctx, "EndIf");
		if (getAppendIf(ctx) != null) {
			getIfNode(getAppendIf(ctx)).addEdge(iff);
			endif.addEdge(getNode(getAppendIf(ctx)));
		}
		if (getAppendList(ctx) != null) {
			getNode(getAppendList(ctx)).addEdge(iff);
		}
		if (getAppendWhile(ctx) != null) {
			endif.addEdge(getWhileNode(getAppendWhile(ctx)));
		}
		setIfNode(ctx, iff);
		setAppendIf(ctx.stat(0), ctx);
		setNode(ctx, endif);
		if (ctx.stat().size() > 1) {
			setAppendIf(ctx.stat(1), ctx);
		}
	}

	@Override public void enterAssignStat(FragmentParser.AssignStatContext ctx) {
		Node node = addNode(ctx, "Assign " + ctx.target().getText() + " to " + ctx.expr().getText());
		if (getAppendIf(ctx) != null) {
			getIfNode(getAppendIf(ctx)).addEdge(node);
			node.addEdge(getNode(getAppendIf(ctx)));
		}
		if (getAppendList(ctx) != null) {
			getNode(getAppendList(ctx)).addEdge(node);
		}
		if (getAppendWhile(ctx) != null) {
			node.addEdge(getWhileNode(getAppendWhile(ctx)));
		}
		setNode(ctx, node);
	}

	@Override public void enterWhileStat(FragmentParser.WhileStatContext ctx) {
		Node cond = addNode(ctx, "While " + ctx.expr().getText());
		if (getAppendIf(ctx) != null) {
			getIfNode(getAppendIf(ctx)).addEdge(cond);
			cond.addEdge(getNode(getAppendIf(ctx)));
		}
		if (getAppendList(ctx) != null) {
			getNode(getAppendList(ctx)).addEdge(cond);
		}
		if (getAppendWhile(ctx) != null) {
			cond.addEdge(getWhileNode(getAppendWhile(ctx)));
		}
		addToAppendList(ctx.stat(), ctx);
		setAppendWhile(ctx.stat(), ctx);
		setWhileNode(ctx, cond);
		setNode(ctx, cond);
	}

	@Override public void enterBlockStat(FragmentParser.BlockStatContext ctx) {
		if (getAppendList(ctx) != null) {
			addToAppendList(ctx.stat(0), getAppendList(ctx));
		}
		if (getAppendIf(ctx) != null) {
			setAppendIf(ctx.stat(0), getAppendIf(ctx));
		}
		if (getAppendWhile(ctx) != null) {
			setAppendWhile(ctx.stat(ctx.stat().size() -1), getAppendWhile(ctx));
		}
		for (int i = 0; i < ctx.stat().size() - 1; i++) {
			addToAppendList(ctx.stat(i+1), ctx.stat(i));
		}
	}

	@Override public void enterPrintStat(FragmentParser.PrintStatContext ctx) {
		Node print = addNode(ctx, "Print");
		if (getAppendIf(ctx) != null) {
			getIfNode(getAppendIf(ctx)).addEdge(print);
			print.addEdge(getNode(getAppendIf(ctx)));
		} else if (getAppendList(ctx) != null) {
			getNode(getAppendList(ctx)).addEdge(print);
		}
		if (getAppendWhile(ctx) != null) {
			print.addEdge(getWhileNode(getAppendWhile(ctx)));
		}
		setNode(ctx, print);
	}

	@Override
	public void enterBreakStat(BreakStatContext ctx) {
		throw new IllegalArgumentException("Break not supported");
	}

	@Override
	public void enterContStat(ContStatContext ctx) {
		throw new IllegalArgumentException("Continue not supported");
	}

	private Node getWhileNode(ParseTree tree) {
		return this.whileNode.get(tree);
	}

	private void setWhileNode(ParseTree tree, Node node) {
		this.whileNode.put(tree, node);
	}

	private void setAppendWhile(ParseTree tree, ParseTree node) {
		this.appendWhile.put(tree, node);
	}

	private ParseTree getAppendWhile(ParseTree tree) {
		return this.appendWhile.get(tree);
	}

	private ParseTree getAppendIf(ParseTree tree) {
		return this.appendIf.get(tree);
	}

	private void setAppendIf(ParseTree tree, ParseTree node) {
		this.appendIf.put(tree, node);
	}

	private Node getIfNode(ParseTree tree) {
		return this.ifNodes.get(tree);
	}

	private void setIfNode(ParseTree tree, Node node) {
		this.ifNodes.put(tree, node);
	}

	private ParseTree getAppendList(ParseTree tree) {
		return this.appendTo.get(tree);
	}

	private void addToAppendList(ParseTree dest, ParseTree self) {
		this.appendTo.put(dest, self);
	}

	private Node getNode(ParseTree tree) {
		return this.nodes.get(tree);
	}

	private void setNode(ParseTree tree, Node node) {
		this.nodes.put(tree, node);
	}

	/** Adds a node to he CGF, based on a given parse tree node.
	 * Gives the CFG node a meaningful ID, consisting of line number and 
	 * a further indicator.
	 */
	private Node addNode(ParserRuleContext node, String text) {
		return this.graph.addNode(node.getStart().getLine() + ": " + text);
	}

	/** Main method to build and print the CFG of a simple Java program. */
	public static void main(String[] args) {
		if (args.length == 0) {
			System.err.println("Usage: [filename]+");
			return;
		}
		TopDownCFGBuilder builder = new TopDownCFGBuilder();
		for (String filename : args) {
			File file = new File(filename);
			System.out.println(filename);
			System.out.println(builder.build(file));
		}
	}
}
