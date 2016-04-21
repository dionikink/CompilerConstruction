package pp.block1.cp.block1;

/**
 * Created by Jens on 20-4-2016.
 *
 */
public class UnsafeSequence {
    private int value;

    public int getNext() {
        return value++;
    }
}
