package pp.block5.cc.simple;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;

import pp.block5.cc.pascal.SimplePascalBaseVisitor;
import pp.block5.cc.pascal.SimplePascalParser;
import pp.iloc.Simulator;
import pp.iloc.model.Label;
import pp.iloc.model.Num;
import pp.iloc.model.Op;
import pp.iloc.model.OpCode;
import pp.iloc.model.Operand;
import pp.iloc.model.Program;
import pp.iloc.model.Reg;

import javax.swing.plaf.synth.SynthOptionPaneUI;

/** Class to generate ILOC code for Simple Pascal. */
public class Generator extends SimplePascalBaseVisitor<Op> {
	/** The representation of the boolean value <code>false</code>. */
	public final static Num FALSE_VALUE = new Num(Simulator.FALSE);
	/** The representation of the boolean value <code>true</code>. */
	public final static Num TRUE_VALUE = new Num(Simulator.TRUE);

	/** The base register. */
	private Reg arp = new Reg("r_arp");
	/** The outcome of the checker phase. */
	private Result checkResult;
	/** Association of statement nodes to labels. */
	private ParseTreeProperty<Label> labels;
	/** The program being built. */
	private Program prog;
	/** Register count, used to generate fresh registers. */
	private int regCount;
	/** Association of expression and target nodes to registers. */
	private ParseTreeProperty<Reg> regs;

	/** Generates ILOC code for a given parse tree,
	 * given a pre-computed checker result.
	 */
	public Program generate(ParseTree tree, Result checkResult) {
		this.prog = new Program();
		this.checkResult = checkResult;
		this.regs = new ParseTreeProperty<>();
		this.labels = new ParseTreeProperty<>();
		this.regCount = 0;
		tree.accept(this);
		return this.prog;
	}

	// Override the visitor methods
	@Override public Op visitProgram(SimplePascalParser.ProgramContext ctx) {
		createLabel(ctx, "start");
		return visitChildren(ctx);
	}

	@Override public Op visitBody(SimplePascalParser.BodyContext ctx) { return visitChildren(ctx); }

	@Override public Op visitVarDecl(SimplePascalParser.VarDeclContext ctx) {

		return visitChildren(ctx);
	}

	@Override public Op visitVar(SimplePascalParser.VarContext ctx) {
		return visitChildren(ctx);
	}

	@Override public Op visitBlock(SimplePascalParser.BlockContext ctx) { return visitChildren(ctx); }
	@Override public Op visitAssStat(SimplePascalParser.AssStatContext ctx) {

		return visitChildren(ctx);
	}

	@Override public Op visitIfStat(SimplePascalParser.IfStatContext ctx) {

		return visitChildren(ctx);
	}

	@Override public Op visitWhileStat(SimplePascalParser.WhileStatContext ctx) { return visitChildren(ctx); }
	@Override public Op visitBlockStat(SimplePascalParser.BlockStatContext ctx) { return visitChildren(ctx); }

	@Override public Op visitInStat(SimplePascalParser.InStatContext ctx) { return visitChildren(ctx); }
	@Override public Op visitOutStat(SimplePascalParser.OutStatContext ctx) { return visitChildren(ctx); }

	@Override public Op visitPrfExpr(SimplePascalParser.PrfExprContext ctx) {
		Op op = null;
		if (checkResult.getType(ctx.expr()) == Type.INT) {
			op = emit(OpCode.mult, new Num(-1), reg(ctx.expr()), reg(ctx.expr()));
		} else if (checkResult.getType(ctx.expr()) == Type.BOOL) {
			op = emit(OpCode.xor, TRUE_VALUE, reg(ctx.expr()), reg(ctx.expr()));
		}
		return op;
	}

	@Override public Op visitParExpr(SimplePascalParser.ParExprContext ctx) {
		return visit(ctx.expr());
	}

	@Override public Op visitBoolExpr(SimplePascalParser.BoolExprContext ctx) {
		visit(ctx.expr(0));
		visit(ctx.expr(1));
		Op bool = visit(ctx.boolOp());
		Reg reg = newReg();
		Reg e0 = reg(ctx.expr(0));
		Reg e1 = reg(ctx.expr(1));
		setReg(ctx, reg);
		return emit(bool.getOpCode(), e0, e1, reg);
	}

	@Override public Op visitCompExpr(SimplePascalParser.CompExprContext ctx) {
		visit(ctx.expr(0));
		visit(ctx.expr(1));
		Op comp = visit(ctx.compOp());
		Reg reg = newReg();
		Reg e0 = reg(ctx.expr(0));
		Reg e1 = reg(ctx.expr(1));
		setReg(ctx, reg);
		return emit(comp.getOpCode(), e0, e1, reg);	}

	@Override public Op visitPlusExpr(SimplePascalParser.PlusExprContext ctx) {
		visit(ctx.expr(0));
		visit(ctx.expr(1));
		Op op = visit(ctx.plusOp());
		Reg reg = newReg();
		Reg e0 = reg(ctx.expr(0));
		Reg e1 = reg(ctx.expr(1));
		setReg(ctx, reg);
		return emit(op.getOpCode(), e0, e1, reg);
	}

	@Override public Op visitMultExpr(SimplePascalParser.MultExprContext ctx) {
		visit(ctx.expr(0));
		visit(ctx.expr(1));
		Op op = visit(ctx.multOp());
		Reg reg = newReg();
		Reg e0 = reg(ctx.expr(0));
		Reg e1 = reg(ctx.expr(1));
		setReg(ctx, reg);
		return emit(op.getOpCode(), e0, e1, reg);
	}

	@Override public Op visitNumExpr(SimplePascalParser.NumExprContext ctx) {
		Reg reg = newReg();
		Num num = new Num(Integer.parseInt(ctx.getText()));
		setReg(ctx, reg);
		return emit(OpCode.loadI, num, reg);
	}

	@Override public Op visitIdExpr(SimplePascalParser.IdExprContext ctx) {
		Reg reg = newReg();
		System.out.println(ctx.getText());
		System.out.println(offset(ctx));
		setReg(ctx, reg);
		return emit(OpCode.loadAI, arp, offset(ctx), reg);
	}

	@Override public Op visitTrueExpr(SimplePascalParser.TrueExprContext ctx) {
		Reg reg = newReg();
		setReg(ctx, reg);
		return emit(OpCode.loadI, TRUE_VALUE, reg);
	}

	@Override public Op visitFalseExpr(SimplePascalParser.FalseExprContext ctx) {
		Reg reg = newReg();
		setReg(ctx, reg);
		return emit(OpCode.loadI, FALSE_VALUE, reg);
	}

	@Override public Op visitMultOp(SimplePascalParser.MultOpContext ctx) {
		Op op = null;
		if (ctx.SLASH() != null) {
			op = new Op(OpCode.div, null, null, null);
		} else if (ctx.STAR() != null) {
			op = new Op(OpCode.mult, null, null, null);
		}
		return op;
	}

	@Override public Op visitPlusOp(SimplePascalParser.PlusOpContext ctx) {
		Op op = null;
		if (ctx.PLUS() != null) {
			op = new Op(OpCode.add, null, null, null);
		} else if (ctx.MINUS() != null) {
			op = new Op(OpCode.sub, null, null, null);
		}
		return op;
	}

	@Override public Op visitBoolOp(SimplePascalParser.BoolOpContext ctx) {
		Op op = null;
		if (ctx.AND() != null) {
			op = new Op(OpCode.and, null, null, null);
		} else if (ctx.OR() != null) {
			op = new Op(OpCode.or, null, null, null);
		}
		return op;
	}

	@Override public Op visitCompOp(SimplePascalParser.CompOpContext ctx) {
		Op op = null;
		if (ctx.LE() != null) {
			op = new Op(OpCode.cmp_LE, null, null, null);
		} else if (ctx.LT() != null) {
			op = new Op(OpCode.cmp_LT, null, null, null);
		} else if (ctx.GE() != null) {
			op = new Op(OpCode.cmp_GE, null, null, null);
		} else if (ctx.GT() != null) {
			op = new Op(OpCode.cmp_GT, null, null, null);
		} else if (ctx.EQ() != null) {
			op = new Op(OpCode.cmp_EQ, null, null, null);
		} else if (ctx.NE() != null) {
			op = new Op(OpCode.cmp_NE, null, null, null);
		}
		return op;
	}
	/*
	@Override public Op visitIntType(SimplePascalParser.IntTypeContext ctx) { return visitChildren(ctx); }
	@Override public Op visitBoolType(SimplePascalParser.BoolTypeContext ctx) { return visitChildren(ctx); }*/


	/** Constructs an operation from the parameters 
	 * and adds it to the program under construction. */
	private Op emit(Label label, OpCode opCode, Operand... args) {
		Op result = new Op(label, opCode, args);
		this.prog.addInstr(result);
		return result;
	}

	/** Constructs an operation from the parameters 
	 * and adds it to the program under construction. */
	private Op emit(OpCode opCode, Operand... args) {
		return emit((Label) null, opCode, args);
	}

	/** 
	 * Looks up the label for a given parse tree node,
	 * creating it if none has been created before.
	 * The label is actually constructed from the entry node
	 * in the flow graph, as stored in the checker result.
	 */
	private Label label(ParserRuleContext node) {
		Label result = this.labels.get(node);
		if (result == null) {
			ParserRuleContext entry = this.checkResult.getEntry(node);
			result = createLabel(entry, "n");
			this.labels.put(node, result);
		}
		return result;
	}

	/** Creates a label for a given parse tree node and prefix. */
	private Label createLabel(ParserRuleContext node, String prefix) {
		Token token = node.getStart();
		int line = token.getLine();
		int column = token.getCharPositionInLine();
		String result = prefix + "_" + line + "_" + column;
		return new Label(result);
	}

	/** Retrieves the offset of a variable node from the checker result,
	 * wrapped in a {@link Num} operand. */
	private Num offset(ParseTree node) {
		return new Num(this.checkResult.getOffset(node));
	}

	/** Returns a register for a given parse tree node,
	 * creating a fresh register if there is none for that node. */
	private Reg reg(ParseTree node) {
		Reg result = this.regs.get(node);
		if (result == null) {
			result = new Reg("r_" + this.regCount);
			this.regs.put(node, result);
			this.regCount++;
		}
		return result;
	}

	/** Assigns a register to a given parse tree node. */
	private void setReg(ParseTree node, Reg reg) {
		this.regs.put(node, reg);
	}

	private Reg newReg() {
		Reg reg = new Reg("r_"+regCount);
		regCount++;
		return reg;
	}
}
