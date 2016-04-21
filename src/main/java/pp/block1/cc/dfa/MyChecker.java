package pp.block1.cc.dfa;

/**
 * Created by Dion on 20-4-2016.
 */
public class MyChecker implements Checker {

    @Override
    public boolean accepts(State start, String word) {
        char[] words = word.toCharArray();

        if(word.equals("")) {
            return start.isAccepting();
        }

        for (int i = 0; i < words.length; i++) {
            State next = start.getNext(words[i]);

            if (next == null) {
                if (start.isAccepting()) {
                    return true;
                } else {
                    return false;
                }
            } else {
                start = next;
            }
        }

        return true;
    }
}
