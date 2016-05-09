package pp.block3.cc.symbol;

import java.util.ArrayList;

/**
 * Created by Jens on 9-5-2016.
 *
 */
public class SymbolTablert implements SymbolTable{

    private ArrayList<ArrayList<String>> table;
    private int deepestScope;

    public SymbolTablert() {
        this.table = new ArrayList<>();
        openScope();
        this.deepestScope = 0;
    }
    @Override
    public void openScope() {
        table.add(new ArrayList<>());
        deepestScope++;
    }

    @Override
    public void closeScope() {
        if (deepestScope != 0) {
            table.remove(deepestScope);
            deepestScope--;
        } else {
            throw (new RuntimeException("Only contains outer scope"));
        }
    }

    @Override
    public boolean add(String id) {
        if (table.get(deepestScope).contains(id)) {
            return false;
        }
        table.get(deepestScope).add(id);
        return true;
    }

    @Override
    public boolean contains(String id) {
        for (int i = 0; i <= deepestScope; i++) {
            if (table.get(i).contains(id)) {
                return true;
            }
        }
        return false;
    }
}
