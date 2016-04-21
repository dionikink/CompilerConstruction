package pp.block1.cc.dfa;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jens on 20-4-2016.
 *
 */
public class MyScanner implements Scanner {

    @Override
    public List<String> scan(State dfa, String text) {
        char[] characters = text.toCharArray();
        List<String> resultList = new ArrayList<>();
        int index = 0;
        boolean noWordsInText = true;

        while (index < characters.length) {

            StringBuilder lexeme = new StringBuilder();
            State currentState = dfa;
            List<State> stack = new ArrayList<>();

            while (index < characters.length && currentState.getNext(characters[index]) != null) {
                currentState = currentState.getNext(characters[index]);
                lexeme.append(characters[index]);
                if (currentState.isAccepting()) {
                    stack = new ArrayList<>();
                }

                stack.add(currentState);
                index++;
            }

            while (stack.size() > 0) {
                int stateIndex = stack.size() - 1;
                State checkState = stack.get(stateIndex);
                if (checkState.isAccepting()) {
                    resultList.add(lexeme.toString());
                    noWordsInText = false;
                    break;
                }
                int lexemeIndex = lexeme.length() - 1;
                stack.remove(stateIndex);
                lexeme.deleteCharAt(lexemeIndex);


                index--;
            }
            if (noWordsInText) {
                break;
            }
        }
        return resultList;
    }
}