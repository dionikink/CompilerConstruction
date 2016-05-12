// Generated from C:/Users/Dion/Documents/TI/Module 8/Compiler Construction/src/main/java/pp/block4/cc/iloc\Calc.g4 by ANTLR 4.5.1
package pp.block4.cc.iloc;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link CalcParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface CalcVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link CalcParser#complete}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComplete(CalcParser.CompleteContext ctx);
	/**
	 * Visit a parse tree produced by the {@code par}
	 * labeled alternative in {@link CalcParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPar(CalcParser.ParContext ctx);
	/**
	 * Visit a parse tree produced by the {@code minus}
	 * labeled alternative in {@link CalcParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMinus(CalcParser.MinusContext ctx);
	/**
	 * Visit a parse tree produced by the {@code number}
	 * labeled alternative in {@link CalcParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumber(CalcParser.NumberContext ctx);
	/**
	 * Visit a parse tree produced by the {@code times}
	 * labeled alternative in {@link CalcParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTimes(CalcParser.TimesContext ctx);
	/**
	 * Visit a parse tree produced by the {@code plus}
	 * labeled alternative in {@link CalcParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPlus(CalcParser.PlusContext ctx);
}