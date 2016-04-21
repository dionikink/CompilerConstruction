package pp.block1.cp.test;

import org.junit.Test;
import pp.block1.cp.queue.QueueEmptyException;
import pp.block1.cp.queue.Queuert;

/**
 * Created by Jens on 20-4-2016.
 *
 */
public class QueueTest {
    private static Queuert queue = new Queuert();

    @Test
    public void testQueue() {
        QueueThread prod1 = new QueueThread(true);
        QueueThread prod2 = new QueueThread(true);
        QueueThread con1 = new QueueThread(false);
        QueueThread con2 = new QueueThread(false);
        prod1.start();
        prod2.start();
        con1.start();
        con2.start();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(prod1.getTotal());
        System.out.println(prod2.getTotal());
        System.out.println(con1.getTotal());
        System.out.println(con2.getTotal());
    }

    private class QueueThread extends Thread{
        boolean isProd;
        int total;
        private QueueThread(boolean isProd) {
            this.isProd = isProd;
        }

        private int getTotal() {
            return total;
        }

        @Override
        public void run() {
            if (isProd) {
                for (int i = 0; i < 5; i++) {
                    total++;
                    queue.push(i);
                }
            } else {
                while (queue.getLength() < 1) {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                while (total < 5) {
                    total++;
                    try {
                        queue.pull();
                    } catch (QueueEmptyException e) {
                        System.err.println("Queue Empty");
                    }
                }
            }
        }
    }
}
