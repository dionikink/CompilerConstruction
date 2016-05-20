package pp.block2.cc.ll;

import com.sun.org.apache.xpath.internal.SourceTree;
import pp.block2.cc.NonTerm;
import pp.block2.cc.Symbol;
import pp.block2.cc.Term;

import java.util.*;

/**
 * Created by Jens on 25-4-2016.
 *
 */
public class LLCalcert implements LLCalc {

    private Grammar grammar;

    private Map<Symbol, Set<Term>> first = null;
    private Map<NonTerm, Set<Term>> follow = null;

    public LLCalcert(Grammar grammar) {
        this.grammar = grammar;
    }

    @Override
    public Map<Symbol, Set<Term>> getFirst() {
        System.out.println("===========================================================");
        System.out.println("\t\t\tFIRST SET");
        System.out.println("===========================================================");
        Map<Symbol, Set<Term>> result = new HashMap<>();

        List<Rule> rules = grammar.getRules();
        Set<Term> terms = grammar.getTerminals();
        Set<NonTerm> nonTerms = grammar.getNonterminals();
        boolean change = true;

        for (Term t : terms) {
            Set<Term> termSet = new HashSet<>();
            termSet.add(t);
            result.put(t, termSet);
        }
        for (NonTerm nt : nonTerms) {
            result.put(nt, new HashSet<>());
        }

        while (change) {
            change = false;
            for (Rule r : rules) {
                System.out.println("Validating rule: " + r);
                System.out.println("So first of: " + r.getLHS());
                List<Symbol> rhsList = r.getRHS();
                Set<Term> rhs = new HashSet<>();
                int j;
                boolean isB = false;
                for (j = 0; j < rhsList.size(); j++) {
                    if (rhsList.get(j) != null) {
                        Set<Term> firstB1 = new HashSet<>();
                        firstB1.addAll(result.get(rhsList.get(j)));
                        if (firstB1.contains(Symbol.EMPTY)) {
                            firstB1.remove(Symbol.EMPTY);
                        }
                        rhs.addAll(firstB1);
                        isB = true;
                        break;
                    }
                }
                if (isB) {
                    Set<Term> firstB = new HashSet<>();
                    firstB.addAll(result.get(rhsList.get(j)));
                    int i = j;
                    int k = rhsList.size();
                    while (firstB.contains(Symbol.EMPTY) && i <= k - 2) {
                        Set<Term> firstBp = new HashSet<>();
                        firstBp.addAll(result.get(rhsList.get(i + 1)));
                        if (firstBp.contains(Symbol.EMPTY)) {
                            firstBp.remove(Symbol.EMPTY);
                        }
                        rhs.addAll(firstBp);
                        i++;
                    }
                    if (i == (k-1) && result.get(rhsList.get(k-1)).contains(Symbol.EMPTY)) {
                        rhs.add(Symbol.EMPTY);
                    }
                    if (!result.get(r.getLHS()).containsAll(rhs)) {
                        System.out.println("------------------------------");
                        System.out.println("Adding: " + rhs);
                        Set<Term> newRHS = result.get(r.getLHS());
                        newRHS.addAll(rhs);
                        System.out.println("Result after adding: " + newRHS);
                        System.out.println("------------------------------");
                        change = true;
                    }
                }
            }
        }
        this.setFirstMap(result);
        System.out.println("===========================================================");
        System.out.println("\t\t\tFIRST SET");
        System.out.println("===========================================================");
        return result;
    }

    @Override
    public Map<NonTerm, Set<Term>> getFollow() {
        System.out.println("===========================================================");
        System.out.println("\t\t\tFOLLOW SET");
        System.out.println("===========================================================");

        Map<NonTerm, Set<Term>> result = new HashMap<>();
        Set<NonTerm> nonTerms = grammar.getNonterminals();
        List<Rule> rules = grammar.getRules();
        if (first == null) {
            getFirst();
        }
        for (NonTerm nt : nonTerms) {
            result.put(nt, new HashSet<>());
        }

        Set<Term> eof = new HashSet<>();
        eof.add(Symbol.EOF);
        result.replace(grammar.getStart(), eof);

        boolean change = true;
        while (change) {
            change = false;
            for (Rule r : rules) {                                      // For each p in P
                System.out.println("Validating rule: " + r);
                Set<Term> trailer = new HashSet<>();
                trailer.addAll(result.get(r.getLHS()));             // Trailer <- Follow(A)
                List<Symbol> rhsList = r.getRHS();

                for (int i = rhsList.size() - 1; i >= 0; i--) {
                    if (rhsList.get(i) instanceof  NonTerm) {           // Bi in NT
                        Set<Term> firstB = new HashSet<>();
                        firstB.addAll(first.get(rhsList.get(i)));

                        if (!result.get(rhsList.get(i)).containsAll(trailer)) {
                            Set<Term> followB = new HashSet<>();
                            followB.addAll(result.get(rhsList.get(i)));
                            followB.addAll(trailer);
                            System.out.println("-----------------------------");
                            System.out.println("Appending follow set of: " + rhsList.get(i));
                            System.out.println("-----------------------------");
                            System.out.println("Added from trailer: " + trailer);
                            System.out.println("Result is now: " + followB);
                            System.out.println("-----------------------------");
                            change = true;
                            result.replace((NonTerm)rhsList.get(i), followB);
                        }
                        if (firstB.contains(Symbol.EMPTY)) {
                            firstB.remove(Symbol.EMPTY);
                            trailer.addAll(firstB);
                            firstB.add(Symbol.EMPTY);
                        } else {
                            trailer = new HashSet<>();
                            trailer.addAll(firstB);
                        }
                    } else {                                                  // Bi not in NT
                        trailer = new HashSet<>();
                        trailer.addAll(first.get(rhsList.get(i)));            // Only set Bi to trailer
                    }
                }
            }
        }
        System.out.println("===========================================================");
        System.out.println("\t\t\tFOLLOW SET");
        System.out.println("===========================================================");
        this.setFollowMap(result);
        return result;
    }

    @Override
    public Map<Rule, Set<Term>> getFirstp() {
        Map<Rule, Set<Term>> result = new HashMap<>();
        List<Rule> rules = grammar.getRules();
        Map<NonTerm, Set<Term>> followMap;
        Map<Symbol, Set<Term>> firstMap;

        if (getFollowMap() == null) {
            followMap = getFollow();
        } else {
            followMap = getFollowMap();
        }
        if (getFirstMap() == null) {
            firstMap = getFirst();
        } else {
            firstMap = getFirstMap();
        }


        for (Rule r : rules) {
            result.put(r, new HashSet<>());
        }
        for (Rule r : rules) {

            List<Symbol> rhs = r.getRHS();
            Set<Term> firstp;

            if (rhs.size() > 0) {
                Set<Term> firstB = new HashSet<>();
                firstB.addAll(firstMap.get(rhs.get(0)));
                if (rhs.get(0) instanceof NonTerm) {
                    if (firstB.contains(Symbol.EMPTY)) {
                        firstB.remove(Symbol.EMPTY);
                        firstp = new HashSet<>();
                        firstp.addAll(firstB);
                        firstB.add(Symbol.EMPTY);

                        firstp.addAll(followMap.get(rhs.get(0)));
                    } else {
                        firstp = new HashSet<>();
                        firstp.addAll(firstB);
                    }
                } else {
                    firstp = new HashSet<>();
                    if (firstB.contains(Symbol.EMPTY)) {
                        firstp.addAll(followMap.get(r.getLHS()));
                    }
                    firstp.addAll(firstB);
                }
                result.replace(r, firstp);
            }
        }
        return result;
    }

    @Override
    public boolean isLL1() {
        Map<Rule, Set<Term>> firstp = getFirstp();
        List<Rule> rules = grammar.getRules();
        List<Rule> lhs = grammar.getRules();
        for (Rule r : rules) {
            for (Rule r1 : lhs) {
                if (!r.equals(r1) && r.getLHS().equals(r1.getLHS())) {
                    if (!Collections.disjoint(firstp.get(r), firstp.get(r1))) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public void setFollowMap(Map<NonTerm, Set<Term>> follow) {
        this.follow = follow;
    }

    public Map<NonTerm, Set<Term>> getFollowMap() {
        return this.follow;
    }

    public void setFirstMap(Map<Symbol, Set<Term>> first) {
        this.first = first;
    }

    public Map<Symbol, Set<Term>> getFirstMap() {
        return this.first;
    }
}
