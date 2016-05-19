package pp.block4.cc.iloc;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import pp.block4.cc.ErrorListener;
import pp.iloc.Simulator;
import pp.iloc.model.*;

/** Compiler from Calc.g4 to ILOC. */
public class CalcCompiler extends CalcBaseListener {
	/** Program under construction. */
	private Program prog;
	private int regCount;
	private ParseTreeProperty<Operand> reg;
	// Attribute maps and other fields

	/** Compiles a given expression string into an ILOC program. */
	public Program compile(String text) {
		Program result = null;
		ErrorListener listener = new ErrorListener();
		CharStream chars = new ANTLRInputStream(text);
		Lexer lexer = new CalcLexer(chars);
		lexer.removeErrorListeners();
		lexer.addErrorListener(listener);
		TokenStream tokens = new CommonTokenStream(lexer);
		CalcParser parser = new CalcParser(tokens);
		parser.removeErrorListeners();
		parser.addErrorListener(listener);
		ParseTree tree = parser.complete();
		if (listener.hasErrors()) {
			System.out.printf("Parse errors in %s:%n", text);
			for (String error : listener.getErrors()) {
				System.err.println(error);
			}
		} else {
			result = compile(tree);
		}
		return result;
	}

	/** Compiles a given Calc-parse tree into an ILOC program. */
	public Program compile(ParseTree tree) {
		prog = new Program();
		regCount = 0;
		reg = new ParseTreeProperty<>();
		ParseTreeWalker walker = new ParseTreeWalker();
		walker.walk(this, tree);
		return prog;
	}

	@Override public void exitComplete(CalcParser.CompleteContext ctx) {
		emit(OpCode.out, new Str("Value: "), getReg(ctx.expr()));
	}
	@Override public void exitPar(CalcParser.ParContext ctx) {
		setReg(ctx, getReg(ctx.expr()));
	}
	@Override public void exitMinus(CalcParser.MinusContext ctx) {
		Reg r = new Reg("r_"+regCount);
		setReg(ctx, r);
		emit(OpCode.rsubI, getReg(ctx.expr()), new Num(0), r);
		regCount++;
	}
	@Override public void exitNumber(CalcParser.NumberContext ctx) {
		Reg r = new Reg("r_"+regCount);
		setReg(ctx, r);
		emit(OpCode.loadI, new Num(Integer.parseInt(ctx.NUMBER().getText())), r);
		regCount++;
	}
	@Override public void exitTimes(CalcParser.TimesContext ctx) {
		Reg r = new Reg("r_"+regCount);
		setReg(ctx, r);
		emit(OpCode.mult, getReg(ctx.expr(0)), getReg(ctx.expr(1)), r);
		regCount++;
	}
	@Override public void exitPlus(CalcParser.PlusContext ctx) {
		Reg r = new Reg("r_"+regCount);
		setReg(ctx, r);
		emit(OpCode.add, getReg(ctx.expr(0)), getReg(ctx.expr(1)), r);
		regCount++;
	}

	@Override public void visitErrorNode(ErrorNode node) { }

	/** Constructs an operation from the parameters
	 * and adds it to the program under construction. */
	private void emit(OpCode opCode, Operand... args) {
		this.prog.addInstr(new Op(opCode, args));
	}
	private Operand getReg(ParseTree tree) {
		return this.reg.get(tree);
	}

	private void setReg(ParseTree tree, Operand op) {
		this.reg.put(tree, op);
	}
	/** Calls the compiler, and simulates and prints the compiled program. */
	public static void main(String[] args) {
		if (args.length == 0) {
			System.err.println("Usage: [expr]+");
			return;
		}
		CalcCompiler compiler = new CalcCompiler();
		for (String expr : args) {
			System.out.println("Processing " + expr);
			Program prog = compiler.compile(expr);
			new Simulator(prog).run();
			System.out.println(prog.prettyPrint());
		}
	}
}
