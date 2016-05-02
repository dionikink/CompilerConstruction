package pp.block2.cc.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import pp.block2.cc.NonTerm;
import pp.block2.cc.Symbol;
import pp.block2.cc.Term;
import pp.block2.cc.ll.*;
import pp.block2.cc.ll.If;

public class LLCalcTest {

	@Test
    public void testIf() {
        Grammar g = Grammars.makeIf();

        NonTerm stat = g.getNonterminal("Stat");
        NonTerm elsePart = g.getNonterminal("ElsePart");

        Term iff = g.getTerminal(If.IF);
        Term cond = g.getTerminal(If.COND);
        Term then = g.getTerminal(If.THEN);
        Term elsse = g.getTerminal(If.ELSE);
        Term assign = g.getTerminal(If.ASSIGN);
        Term eof = Symbol.EOF;
        Term empty = Symbol.EMPTY;

        LLCalc calc = createCalc(g);
        // Start filling the answers
        Set<Term> firstStat = new HashSet<>();
        firstStat.add(assign);
        firstStat.add(iff);
        Set<Term> firstElsePart = new HashSet<>();
        firstElsePart.add(elsse);
        firstElsePart.add(empty);
        Map<Symbol, Set<Term>> first = calc.getFirst();

        assertEquals(firstStat, first.get(stat));
        assertEquals(firstElsePart, first.get(elsePart));

        Map<NonTerm, Set<Term>> follow = calc.getFollow();
        Set<Term> followStat = new HashSet<>();
        followStat.add(eof);
        followStat.add(elsse);
        Set<Term> followElsePart = new HashSet<>();
        followElsePart.add(eof);
        followElsePart.add(elsse);
		assertEquals(followStat, follow.get(stat));
        assertEquals(followElsePart, follow.get(elsePart));

        Map<Rule, Set<Term>> firstp = calc.getFirstp();
		List<Rule> elsePartRules = g.getRules(elsePart);
		List<Rule> statRules = g.getRules(stat);

        Set<Term> firstpep0 = new HashSet<>();
        firstpep0.add(elsse);
        Set<Term> firstpep1 = new HashSet<>();
		firstpep1.add(elsse);
        firstpep1.add(empty);
        firstpep1.add(eof);
		Set<Term> firstpstat1 = new HashSet<>();
		firstpstat1.add(iff);
		Set<Term> firstpstat0 = new HashSet<>();
		firstpstat0.add(assign);

        assertEquals(firstpep0, firstp.get(elsePartRules.get(0)));
        assertEquals(firstpep1, firstp.get(elsePartRules.get(1)));
		assertEquals(firstpstat0, firstp.get(statRules.get(0)));
		assertEquals(firstpstat1, firstp.get(statRules.get(1)));
	}

	/** Tests the LL-calculator for the Sentence grammar. */
	@Test
	public void testSentenceOrig() {
		Grammar g = Grammars.makeSentence();
		// Without the last (recursive) rule, the grammar is LL-1
        assertTrue(createCalc(g).isLL1());
	}

	@Test
	public void testSentenceExtended() {
		Grammar g = Grammars.makeSentence();
		// Without the last (recursive) rule, the grammar is LL-1
		assertTrue(createCalc(g).isLL1());
		// Now add the last rule, causing the grammar to fail
		// Define the non-terminals
		NonTerm subj = g.getNonterminal("Subject");
		NonTerm obj = g.getNonterminal("Object");
		NonTerm sent = g.getNonterminal("Sentence");
		NonTerm mod = g.getNonterminal("Modifier");
		g.addRule(mod, mod, mod);
		// Define the terminals
		Term adj = g.getTerminal(Sentence.ADJECTIVE);
		Term noun = g.getTerminal(Sentence.NOUN);
		Term verb = g.getTerminal(Sentence.VERB);
		Term end = g.getTerminal(Sentence.ENDMARK);
		LLCalc calc = createCalc(g);
		// FIRST sets
		Map<Symbol, Set<Term>> first = calc.getFirst();
		assertEquals(set(adj, noun), first.get(sent));
		assertEquals(set(adj, noun), first.get(subj));
		assertEquals(set(adj, noun), first.get(obj));
		assertEquals(set(adj), first.get(mod));
		// FOLLOW sets
		Map<NonTerm, Set<Term>> follow = calc.getFollow();
		assertEquals(set(Symbol.EOF), follow.get(sent));
		assertEquals(set(verb), follow.get(subj));
		assertEquals(set(end), follow.get(obj));
		assertEquals(set(noun, adj), follow.get(mod));
		// FIRST+ sets: test per rule
		Map<Rule, Set<Term>> firstp = calc.getFirstp();
		List<Rule> subjRules = g.getRules(subj);
		assertEquals(set(noun), firstp.get(subjRules.get(0)));
		assertEquals(set(adj), firstp.get(subjRules.get(1)));
		// is-LL1-test
		assertFalse(calc.isLL1());
	}

	/** Creates an LL1-calculator for a given grammar. */
	private LLCalc createCalc(Grammar g) {
		return new LLCalcert(g); // your implementation of LLCalc (Ex. 2-CC.5)
	}

	@SuppressWarnings("unchecked")
	private <T> Set<T> set(T... elements) {
		return new HashSet<>(Arrays.asList(elements));
	}
}
