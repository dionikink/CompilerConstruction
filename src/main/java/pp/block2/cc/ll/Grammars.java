/**
 * 
 */
package pp.block2.cc.ll;

import pp.block2.cc.NonTerm;
import pp.block2.cc.Symbol;
import pp.block2.cc.SymbolFactory;
import pp.block2.cc.Term;

/**
 * Class containing some example grammars.
 * @author Arend Rensink
 *
 */
public class Grammars {
	/** Returns a grammar for simple English sentences. */
	public static Grammar makeSentence() {
		// Define the non-terminals
		NonTerm sent = new NonTerm("Sentence");
		NonTerm subj = new NonTerm("Subject");
		NonTerm obj = new NonTerm("Object");
		NonTerm mod = new NonTerm("Modifier");
		// Define the terminals, using the Sentence.g4 lexer grammar
		SymbolFactory fact = new SymbolFactory(Sentence.class);
		Term noun = fact.getTerminal(Sentence.NOUN);
		Term verb = fact.getTerminal(Sentence.VERB);
		Term adj = fact.getTerminal(Sentence.ADJECTIVE);
		Term end = fact.getTerminal(Sentence.ENDMARK);
		// Build the context free grammar
		Grammar g = new Grammar(sent);
		g.addRule(sent, subj, verb, obj, end);
		g.addRule(subj, noun);
		g.addRule(subj, mod, subj);
		g.addRule(obj, noun);
		g.addRule(obj, mod, obj);
		g.addRule(mod, adj);
		return g;
	}

	public static Grammar makeIf() {
        NonTerm stat = new NonTerm("Stat");
        NonTerm elsepart = new NonTerm("ElsePart");
        SymbolFactory fact = new SymbolFactory(If.class);
        Term assign = fact.getTerminal(If.ASSIGN);
        Term iff = fact.getTerminal(If.IF);
        Term cond = fact.getTerminal(If.COND);
        Term then = fact.getTerminal(If.THEN);
        Term elsse = fact.getTerminal(If.ELSE);
        Grammar g = new Grammar(stat);
        g.addRule(stat, assign);
        g.addRule(stat, iff, cond, then, stat, elsepart);
        g.addRule(elsepart, elsse, stat);
        g.addRule(elsepart, Symbol.EMPTY);
        return g;
    }

	public static Grammar makeLQR() {
		NonTerm l = new NonTerm("L");
		NonTerm r = new NonTerm("R");
		NonTerm q = new NonTerm("Q");
		NonTerm h = new NonTerm("H");
		NonTerm ra = new NonTerm("Ra");
		SymbolFactory fact = new SymbolFactory(LQR.class);
		Term a = fact.getTerminal(LQR.A);
		Term b = fact.getTerminal(LQR.B);
		Term c = fact.getTerminal(LQR.C);
		Grammar g = new Grammar(l);
		g.addRule(l, r, a);
		g.addRule(l, q, b, a);
		g.addRule(r, a, b, a, ra);
		g.addRule(r, c, a, b, a, ra);
		g.addRule(ra, b, c, ra);
		g.addRule(ra, Symbol.EMPTY);
		g.addRule(q, b, h);
		g.addRule(h, b, c);
		g.addRule(h, c);
		return g;
	}
}
