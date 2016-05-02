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
        if (queue.size() > 0) {
            Object o = queue.getLast();
            queue.removeLast();
            return o;
        } else {
            throw new QueueEmptyException();
        }
    }

    @Override
    public synchronized int getLength() {
        return queue.size();
    }
}
