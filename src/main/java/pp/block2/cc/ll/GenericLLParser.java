package pp.block2.cc.ll;

import java.util.*;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Token;

import pp.block2.cc.AST;
import pp.block2.cc.NonTerm;
import pp.block2.cc.ParseException;
import pp.block2.cc.Parser;
import pp.block2.cc.Symbol;
import pp.block2.cc.Term;

/** Generic table-driven LL(1)-parser. */
public class GenericLLParser implements Parser {
	/** The grammar underlying this parser instance. */
	private final Grammar g;
	/** The LL-calculater for the grammar. */
	private final LLCalc calc;
	/** Map from non-terminals to maps of rules indexed by terminal. */
	private Map<NonTerm, Map<Term, Rule>> ll1Table;
	/** Current index in the token list. */
	private int index;
	/** Token list of the currently parsed input. */
	private List<? extends Token> tokens;

	private Map<Rule, Set<Term>> firstp;


	public GenericLLParser(Grammar g) {
		this.g = g;
		this.calc = new LLCalcert(g); // here use your own class
	}

	@Override
	public AST parse(Lexer lexer) throws ParseException {
		this.tokens = lexer.getAllTokens();
		this.index = 0;
		this.firstp = calc.getFirstp();
		return parse(this.g.getStart());
	}

	/** Parses the start of the token stream according to a given
	 * symbol. If it is a terminal, that should be the first token;
	 * otherwise, it is a non-terminal that should be expanded 
	 * according to the next token in the token stream, using the
	 * FIRST+-lookup table and recursively calling {@link #parse(Rule)}
	 * @param symb the symbol according to which the token stream 
	 * should be parsed
	 * @return the sub-AST resulting from the parsing of symb;
	 * or null if the symbol expands to the empty string
	 * @throws ParseException if the symbol cannot be parsed
	 * because the token stream does not contain the expected symbols
	 */
	private AST parse(Symbol symb) throws ParseException {
		AST result;

		if (symb instanceof NonTerm) {
			result = parse(lookup((NonTerm) symb));
		} else {
			Token token = next();
			if (((Term) symb).getTokenType() != token.getType()) {
				throw (new ParseException("fail"));
			}
			result = new AST((Term) symb, token);
		}
        return result;
	}

	/** Parses the start of the token stream according to a given
	 * rule, recursively calling {@link #parse(Symbol)} to process
	 * the RHS.
	 * @return the sub-AST resulting from the parsing of the rule.
	 * The top node is the node for the LHS of the rule, its direct
	 * children correspond to the symbols of the rule's RHS.
	 * @throws ParseException if the symbol cannot be parsed
	 * because the token stream does not contain the expected symbols
	 */
	private AST parse(Rule rule) throws ParseException {
		NonTerm nonTerm = rule.getLHS();
		AST result = new AST(nonTerm);
		for (Symbol s : rule.getRHS()) {
			if (s != Symbol.EMPTY) {
				result.addChild(parse(s));
			}
		}
        return result;
	}

	/** Uses the lookup table to look up the rule to which
	 * a given nonterminal should be expanded.
	 * The next rule is determined by the next token, using the
	 * FIRST+-set of the nonterminal.
	 * @throws ParseException if the lookup table does not 
	 * contain a rule for the nonterminal in combination with
	 * the first token in the token stream.
	 */
	private Rule lookup(NonTerm nt) throws ParseException {
		Rule result;
		if (atEnd()) {
			result = getLL1Table().get(nt).get(Term.EOF);
			if (result == null) {
				throw new ParseException("Reading beyond end of input");
			}
		} else {
			Token nextToken = peek();
			Term term = this.g.getTerminal(nextToken.getType());
			result = getLL1Table().get(nt).get(term);
			if (result == null) {
				throw new ParseException(String.format(
						"Line %d:%d - no rule for '%s' on token '%s'",
						nextToken.getLine(), nextToken.getCharPositionInLine(),
						nt.getName(), nextToken));
			}
		}
		return result;
	}

	/** Tests whether the end of input has been reached. */
	private boolean atEnd() {
		return this.index >= this.tokens.size();
	}

	/** Returns the next token, without moving the token index. */
	private Token peek() throws ParseException {
		if (this.index >= this.tokens.size()) {
			throw new ParseException("Reading beyond end of input");
		}
		return this.tokens.get(this.index);
	}

	/** Returns the next token and moves up the token index. */
	private Token next() throws ParseException {
		Token result = peek();
		this.index++;
		return result;
	}

	/** Lazily builds and then returns the lookup table. */
	private Map<NonTerm, Map<Term, Rule>> getLL1Table() {
		if (this.ll1Table == null) {
			this.ll1Table = calcLL1Table();
		}
		return this.ll1Table;
	}

	/** Constructs the {@link #ll1Table}. */
	private Map<NonTerm, Map<Term, Rule>> calcLL1Table() {
		Map<NonTerm, Map<Term, Rule>> result = new HashMap<>();
		Set<NonTerm> nonTerms = g.getNonterminals();
		Set<Term> terms = g.getTerminals();
		List<Rule> rules;

		for (NonTerm nt : nonTerms) {
			Map<Term, Rule> termRuleMap = new HashMap<>();
			for (Term t : terms) {
				termRuleMap.put(t, null);
			}
			rules = g.getRules(nt);
			for (Rule r : rules) {
				Set<Term> wSet = firstp.get(r);
				for (Term w : wSet) {
					termRuleMap.replace(w, r);
				}
				if (firstp.get(r).contains(Symbol.EOF)) {
					termRuleMap.put(Symbol.EOF, r);
				}
			}
			result.put(nt, termRuleMap);
		}
		return result;
	}
}
