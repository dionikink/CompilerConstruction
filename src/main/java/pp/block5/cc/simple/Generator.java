package pp.block5.cc.simple;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;

import pp.block5.cc.pascal.SimplePascalBaseVisitor;
import pp.block5.cc.pascal.SimplePascalParser;
import pp.iloc.Simulator;
import pp.iloc.model.*;

import java.util.HashMap;
import java.util.Map;

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
	private ParseTreeProperty<ParserRuleContext> next;
	private Map<String, Reg> regMap;
	private static ParserRuleContext END;

	/** Generates ILOC code for a given parse tree,
	 * given a pre-computed checker result.
	 */
	public Program generate(ParseTree tree, Result checkResult) {
		this.prog = new Program();
		this.checkResult = checkResult;
		this.regs = new ParseTreeProperty<>();
		this.labels = new ParseTreeProperty<>();
		this.next = new ParseTreeProperty<>();
		this.regCount = 0;
		this.regMap = new HashMap<>();
		tree.accept(this);
		return this.prog;
	}

	// Override the visitor methods
	@Override public Op visitProgram(SimplePascalParser.ProgramContext ctx) {
		END = ctx;
		visitChildren(ctx);
		if (hasLabel(ctx)) {
			emit(label(ctx), OpCode.nop);
		}
		return null;
	}

	@Override public Op visitBody(SimplePascalParser.BodyContext ctx) { return visitChildren(ctx); }

	@Override public Op visitVarDecl(SimplePascalParser.VarDeclContext ctx) {
		return visitChildren(ctx);
	}

	@Override public Op visitVar(SimplePascalParser.VarContext ctx) {
		for (int i = 0; i < ctx.ID().size(); i++) {
			Reg reg = reg(ctx.ID(i));
			setRegManually(ctx.ID(i).getText(), reg);
		}
		return null;
	}

	@Override public Op visitBlock(SimplePascalParser.BlockContext ctx) {
		for (int i = 0; i < ctx.stat().size(); i++) {
			if (i == ctx.stat().size() - 1) {
				if (getNext(ctx) == null) {
					setNext(ctx.stat(i), END);
				} else {
					setNext(ctx.stat(i), getNext(ctx));
				}
			} else {
				setNext(ctx.stat(i), checkResult.getEntry(ctx.stat(i + 1)));
			}
		}
		visitChildren(ctx);
		return null;
	}

	@Override public Op visitAssStat(SimplePascalParser.AssStatContext ctx) {
		boolean hasLabel = hasLabel(ctx);
		visit(ctx.target());
		if (hasLabel) {
			label(ctx.expr());
		}
		Op expr0 = visit(ctx.expr());
		if (expr0 == null && hasLabel) {
			return emit(label(ctx), OpCode.i2i, reg(ctx.expr()), reg(ctx.target()));
		} else {
			return emit(OpCode.i2i, reg(ctx.expr()), reg(ctx.target()));
		}
	}

	@Override public Op visitIfStat(SimplePascalParser.IfStatContext ctx) {
		Op result;
		setNext(ctx.stat(0), getNext(ctx));
		if (hasLabel(ctx.expr())) {
			setLabelManually(ctx, label(ctx.expr()));
		} else {
			label(ctx.expr());
		}
		label(ctx.stat(0));
		Label next;
		if (getNext(ctx) != END) {
			next = label(getNext(ctx));
		} else {
			Label label = new Label("END");
			setLabelManually(END, label);
			next = label(END);
		}
		Op expr = visit(ctx.expr());
		if (ctx.ELSE() == null) {
			if (expr == null && hasLabel(ctx)) {
				emit(label(ctx), OpCode.cbr,  reg(ctx.expr()), label(ctx.stat(0)), next);
			} else {
				emit(OpCode.cbr, reg(ctx.expr()), label(ctx.stat(0)), next);
			}
			visit(ctx.stat(0));
			result = emit(OpCode.jumpI, next);
		} else {
			label(ctx.stat(1));
			if (expr == null && hasLabel(ctx)) {
				emit(label(ctx), OpCode.cbr, reg(ctx.expr()), label(ctx.stat(0)), label(ctx.stat(1)));
			} else {
				emit(OpCode.cbr, reg(ctx.expr()), label(ctx.stat(0)), label(ctx.stat(1)));
			}
			setNext(ctx.stat(1), getNext(ctx));
			visit(ctx.stat(0));
			emit(OpCode.jumpI, next);
			visit(ctx.stat(1));
			result = emit(OpCode.jumpI, next);
		}
		return result;
	}

	@Override public Op visitWhileStat(SimplePascalParser.WhileStatContext ctx) {
		Op result;
		label(ctx.expr());
		label(ctx.stat());
		label(getNext(ctx));
		setNext(ctx.stat(), ctx);
		visit(ctx.expr());
		emit(OpCode.cbr, reg(ctx.expr()), label(ctx.stat()), label(getNext(ctx)));
		visit(ctx.stat());
		result = emit(OpCode.jumpI, label(ctx.expr()));
		return result;
	}

	@Override public Op visitBlockStat(SimplePascalParser.BlockStatContext ctx) {
		setNext(ctx.block(), getNext(ctx));
		if (hasLabel(ctx)) {
			label(ctx.block().stat(0));
		}
		return visit(ctx.block());
	}

	@Override public Op visitInStat(SimplePascalParser.InStatContext ctx) {
		Op result;
		visit(ctx.target());
		String s = ctx.STR().getText();
		String str = s.substring(1, s.length() - 1);
		if (hasLabel(ctx)) {
			result = emit(label(ctx), OpCode.in, new Str(str), reg(ctx.target()));
		} else {
			result = emit(OpCode.in, new Str(str), reg(ctx.target()));
		}
		return result;
	}

	@Override public Op visitOutStat(SimplePascalParser.OutStatContext ctx) {
		Op result;
		if (hasLabel(ctx)) {
			setLabelManually(ctx.expr(), label(ctx));
		}
		Op expr = visit(ctx.expr());
		String s = ctx.STR().getText();
		String str = s.substring(1, s.length() - 1);
		if (hasLabel(ctx) && expr == null && !isNumber(ctx.expr())) {
			result = emit(label(ctx), OpCode.out, new Str(str), reg(ctx.expr()));
		} else {
			result = emit(OpCode.out, new Str(str), reg(ctx.expr()));
		}
		return result;
	}

	@Override public Op visitIdTarget(SimplePascalParser.IdTargetContext ctx) {
		reg(ctx);
		return null;
	}

	@Override public Op visitPrfExpr(SimplePascalParser.PrfExprContext ctx) {
		Op result;
		boolean hasLabel = hasLabel(ctx);
		if (hasLabel) {
			label(ctx.expr());
		}
		setNext(ctx.expr(), getNext(ctx));
		Op expr = visit(ctx.expr());
		Operand one = reg(ctx.expr());
		reg(ctx);
		if (checkResult.getType(ctx.expr()) == Type.BOOL) {
			if (expr == null && hasLabel) {
				result = emit(label(ctx), OpCode.xorI, one, TRUE_VALUE, reg(ctx));
			} else {
				result = emit(OpCode.xorI, one, TRUE_VALUE, reg(ctx));
			}
		} else {
			result = null;
		}
		return result;
	}

	@Override public Op visitParExpr(SimplePascalParser.ParExprContext ctx) {
		setNext(ctx.expr(), getNext(ctx));
		if (hasLabel(ctx)) {
			setLabelManually(ctx.expr(), label(ctx));
		}
		visit(ctx.expr());
		setRegManually(ctx.getText(), reg(ctx.expr()));
		return null;
	}

	@Override public Op visitBoolExpr(SimplePascalParser.BoolExprContext ctx) {
		Op result;
		boolean hasLabel = hasLabel(ctx);
		Op op = visit(ctx.boolOp());
		reg(ctx);
		if (hasLabel) {
			label(ctx.expr(0));
		}
		Op expr = visit(ctx.expr(0));
		visit(ctx.expr(1));
		Operand one = reg(ctx.expr(0));
		Operand two = reg(ctx.expr(1));
		if (hasLabel && expr == null) {
			result = emit(label(ctx), op.getOpCode(), one, two, reg(ctx));
		} else {
			result = emit(op.getOpCode(), one, two, reg(ctx));
		}
		return result;
	}

	@Override public Op visitCompExpr(SimplePascalParser.CompExprContext ctx) {
		Op result;
		boolean hasLabel = hasLabel(ctx);
		Op op = visit(ctx.compOp());
		reg(ctx);
		if (hasLabel) {
			label(ctx.expr(0));
		}

		Op expr = visit(ctx.expr(0));
		visit(ctx.expr(1));
		Operand one = reg(ctx.expr(0));
		Operand two = reg(ctx.expr(1));
		if (hasLabel && expr == null) {
			result = emit(label(ctx), op.getOpCode(), one, two, reg(ctx));
		} else {
			result = emit(op.getOpCode(), one, two, reg(ctx));
		}
		return result;
	}

	@Override public Op visitPlusExpr(SimplePascalParser.PlusExprContext ctx) {
		Op result;
		boolean hasLabel = hasLabel(ctx);
		boolean labelSet = false;
		Op op = visit(ctx.plusOp());
		reg(ctx);
		if (hasLabel && !isNumber(ctx.expr(0)) && !isId(ctx.expr(0))) {
			label(ctx.expr(0));
		} else if (!isId(ctx.expr(1))){
			labelSet = true;
			setLabelManually(ctx.expr(1), label(ctx));
		}

		Op expr = visit(ctx.expr(0));
		visit(ctx.expr(1));
		Operand one = reg(ctx.expr(0));
		Operand two = reg(ctx.expr(1));
		if (hasLabel && expr == null && !labelSet) {
			result = emit(label(ctx), op.getOpCode(), one, two, reg(ctx));
		} else {
			result = emit(op.getOpCode(), one, two, reg(ctx));
		}
		return result;
	}

	@Override public Op visitMultExpr(SimplePascalParser.MultExprContext ctx) {
		Op result;
		boolean hasLabel = hasLabel(ctx);
		boolean labelSet = false;
		Op op = visit(ctx.multOp());
		reg(ctx);
		if (hasLabel && !isNumber(ctx.expr(0)) && !isId(ctx.expr(0))) {
			label(ctx.expr(0));
		} else if (!isNumber(ctx.expr(1)) && !isId(ctx.expr(1))){
			labelSet = true;
			setLabelManually(ctx.expr(1), label(ctx));
		}

		Op expr = visit(ctx.expr(0));
		visit(ctx.expr(1));
		Operand one = reg(ctx.expr(0));
		Operand two = reg(ctx.expr(1));
		if (hasLabel && expr == null && !labelSet) {
			result = emit(label(ctx), op.getOpCode(), one, two, reg(ctx));
		} else {
			result = emit(op.getOpCode(), one, two, reg(ctx));
		}
		return result;
	}

	@Override public Op visitNumExpr(SimplePascalParser.NumExprContext ctx) {
		reg(ctx);
		if (hasLabel(ctx)) {
			emit(label(ctx), OpCode.loadI, new Num(Integer.parseInt(ctx.getText())), reg(ctx));
		} else {
			emit(OpCode.loadI, new Num(Integer.parseInt(ctx.getText())), reg(ctx));
		}
		return null;
	}

	@Override public Op visitIdExpr(SimplePascalParser.IdExprContext ctx) {
		reg(ctx);
		return null;
	}

	@Override public Op visitTrueExpr(SimplePascalParser.TrueExprContext ctx) {
		reg(ctx);
		if (hasLabel(ctx)) {
			emit(label(ctx), OpCode.loadI, TRUE_VALUE, reg(ctx));
		} else {
			emit(OpCode.loadI, TRUE_VALUE, reg(ctx));
		}
		return null;
	}

	@Override public Op visitFalseExpr(SimplePascalParser.FalseExprContext ctx) {
		reg(ctx);
		if (hasLabel(ctx)) {
			emit(label(ctx), OpCode.loadI, FALSE_VALUE, reg(ctx));
		} else {
			emit(OpCode.loadI, FALSE_VALUE, reg(ctx));
		}
		return null;
	}

	@Override public Op visitMultOp(SimplePascalParser.MultOpContext ctx) {
		Op op = null;
		if (ctx.SLASH() != null) {
			op = new Op(OpCode.div, arp, arp, arp);
		} else if (ctx.STAR() != null) {
			op = new Op(OpCode.mult, arp, arp, arp);
		}
		return op;
	}

	@Override public Op visitPlusOp(SimplePascalParser.PlusOpContext ctx) {
		Op op = null;
		if (ctx.PLUS() != null) {
			op = new Op(OpCode.add, arp, arp, arp);
		} else if (ctx.MINUS() != null) {
			op = new Op(OpCode.sub, arp, arp, arp);
		}
		return op;
	}

	@Override public Op visitBoolOp(SimplePascalParser.BoolOpContext ctx) {
		Op op = null;
		if (ctx.AND() != null) {
			op = new Op(OpCode.and, arp, arp, arp);
		} else if (ctx.OR() != null) {
			op = new Op(OpCode.or, arp, arp, arp);
		}
		return op;
	}

	@Override public Op visitCompOp(SimplePascalParser.CompOpContext ctx) {
		Op op = null;
		if (ctx.LE() != null) {
			op = new Op(OpCode.cmp_LE, arp, arp, arp);
		} else if (ctx.LT() != null) {
			op = new Op(OpCode.cmp_LT, arp, arp, arp);
		} else if (ctx.GE() != null) {
			op = new Op(OpCode.cmp_GE, arp, arp, arp);
		} else if (ctx.GT() != null) {
			op = new Op(OpCode.cmp_GT, arp, arp, arp);
		} else if (ctx.EQ() != null) {
			op = new Op(OpCode.cmp_EQ, arp, arp, arp);
		} else if (ctx.NE() != null) {
			op = new Op(OpCode.cmp_NE, arp, arp, arp);
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

	private boolean hasLabel(ParserRuleContext node) {
		if (this.labels.get(node) != null) {
			return true;
		} else {
			return false;
		}
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
		return this.labels.get(node);
	}

	/** Creates a label for a given parse tree node and prefix. */
	private Label createLabel(ParserRuleContext node, String prefix) {
		Token token = node.getStart();
		int line = token.getLine();
		int column = token.getCharPositionInLine();
		String result = prefix + "_" + line + "_" + column;
		Label label = new Label(result);
		this.labels.put(node, label);
		return label;
	}

	private void setLabelManually(ParserRuleContext node, Label label) {
		this.labels.put(node, label);
	}

	/** Retrieves the offset of a variable node from the checker result,
	 * wrapped in a {@link Num} operand. */
	private Num offset(ParseTree node) {
		return new Num(this.checkResult.getOffset(node));
	}

	private boolean isNumber(ParseTree node) {
		return (node instanceof SimplePascalParser.NumExprContext);
	}
	private boolean isId(ParseTree node) {
		return (node instanceof SimplePascalParser.IdExprContext);
	}

	private void setRegManually(String text, Reg reg) {
		this.regMap.put(text, reg);
	}

	private Reg reg(ParseTree node) {
		String text = node.getText();
		Reg result = this.regMap.get(text);
		if (result == null) {
			result = new Reg("r_"+ this.regCount);
			this.regMap.put(text, result);
			regCount++;
		}
		return result;
	}

	/** Returns a register for a given parse tree node,
	 * creating a fresh register if there is none for that node. */
//	private Reg reg(ParseTree node) {
//		Reg result = this.regs.get(node);
//		if (result == null) {
//			System.out.println(node);
//			result = new Reg("r_" + this.regCount);
//			this.regs.put(node, result);
//			this.regCount++;
//		}
//		return result;
//	}

	private ParserRuleContext getNext(ParseTree tree) {
		return this.next.get(tree);
	}

	private void setNext(ParseTree tree, ParserRuleContext node) {
		this.next.put(tree, node);
	}

	/** Assigns a register to a given parse tree node. */
	private void setReg(ParseTree node, Reg reg) {
		this.regs.put(node, reg);
	}
}
