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
        QueueThread prod3 = new QueueThread(true);

        QueueThread cons1 = new QueueThread(false);
        QueueThread cons2 = new QueueThread(false);
        QueueThread cons3 = new QueueThread(false);

        prod1.start();
        prod2.start();
        prod3.start();
        cons1.start();
        cons2.start();
        cons3.start();

        try {
            prod1.join();
            prod2.join();
            prod3.join();
            cons1.join();
            cons2.join();
            cons3.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(queue.getLength());
    }

    public class QueueThread extends Thread {
        private boolean isProd;

        public QueueThread(boolean isProd) {
            this.isProd = isProd;
        }

        @Override
        public void run() {

            if(isProd) {
                for(int i = 0; i <= 1000; i++) {
                    queue.push(i);
                }

            } else {
                for(int i = 0; i <= 50000; i++) {
                    try {
                        queue.pull();
                    } catch (QueueEmptyException e) {

                    }
                }
            }
        }
    }
}
