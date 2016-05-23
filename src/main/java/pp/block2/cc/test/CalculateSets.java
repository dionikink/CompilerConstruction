package pp.block2.cc.test;

import org.junit.Test;
import pp.block2.cc.NonTerm;
import pp.block2.cc.Symbol;
import pp.block2.cc.Term;
import pp.block2.cc.ll.Grammar;
import pp.block2.cc.ll.LLCalcert;
import pp.block2.cc.ll.Rule;

import java.util.Map;
import java.util.Set;

/**
 * Created by Jens on 20-5-2016.
 *
 */
public class CalculateSets {

    @Test
    public void test() {
        NonTerm e = new NonTerm("E");
        NonTerm ea = new NonTerm("E'");
        NonTerm f = new NonTerm("F");
        NonTerm t = new NonTerm("T");
        NonTerm ta = new NonTerm("T'");
        Term plus = new Term("+");
        Term min = new Term("-");
        Term ID = new Term("ID");
        Grammar g = new Grammar(e);
        g.addRule(e, f, ea);
        g.addRule(ea, plus, f, ea);
        g.addRule(ea, Symbol.EMPTY);
        g.addRule(f, min, min, f);
        g.addRule(f, t);
        g.addRule(t, ID, ta);
        g.addRule(ta, plus, plus, ta);
        g.addRule(ta, Symbol.EMPTY);

        LLCalcert cal = new LLCalcert(g);
        cal.getFirst();
        System.out.println("**********************************************");
        System.out.println("**********************************************");
        System.out.println("**********************************************");
        System.out.println("**********************************************");
        System.out.println("**********************************************");
        cal.getFollow();
        System.out.println("**********************************************");
        System.out.println("**********************************************");
        System.out.println("**********************************************");
        System.out.println("**********************************************");
        System.out.println("**********************************************");
        Map<Rule, Set<Term>> firstp = cal.getFirstp();
        System.out.println("**********************************************");
        System.out.println("**********************************************");
        System.out.println("\t\t\tFINAL RESULTS");
        System.out.println("**********************************************");
        System.out.println("**********************************************");
        System.out.println(cal.getFirstMap());
        System.out.println(cal.getFollowMap());
        System.out.println(firstp);
    }
}
