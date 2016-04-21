package pp.block1.cp.queue;

import java.util.LinkedList;

/**
 * Created by Jens on 20-4-2016.
 *
 */
public class Queuert implements Queue {
    private static LinkedList<Object> queue = new LinkedList<>();

    @Override
    public synchronized void push(Object x) {
        queue.push(x);
    }

    @Override
    public synchronized Object pull() throws QueueEmptyException {
        Object o = queue.getLast();
        queue.removeLast();
        return o;
    }

    @Override
    public synchronized int getLength() {
        return queue.size();
    }
}
