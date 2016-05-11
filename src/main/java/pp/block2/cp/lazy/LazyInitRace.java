package pp.block2.cp.lazy;

/**
 * Created by Dion on 2-5-2016.
 */
public class LazyInitRace {
    private ExpensiveObject instance = null;

    public ExpensiveObject getInstance() {
        if (instance == null) {
            instance = new ExpensiveObject();
        }

        return instance;
    }

}


