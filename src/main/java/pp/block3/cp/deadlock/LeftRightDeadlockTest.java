package pp.block3.cp.deadlock;

import org.junit.Before;
import org.junit.Test;

/**
 * Created by Dion on 10-5-2016.
 */
public class LeftRightDeadlockTest {
    private LeftRightDeadlock lrd;

    @Before
    public void setUp() {
        this.lrd = new LeftRightDeadlock();
    }

    @Test
    public void runBoth() {
        Thread threadLeft = new LeftRightDeadlockThread(true);
        Thread threadRight = new LeftRightDeadlockThread(false);

        threadLeft.start();
        threadRight.start();

        try {
            threadLeft.join();
            threadRight.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private class LeftRightDeadlockThread extends Thread {
        private boolean left;

        private LeftRightDeadlockThread(boolean left) {
            this.left = left;
        }

        public void run() {
            for(int i = 0; i < 10; i++) {
                if (left) {
                    lrd.leftRight();
                } else {
                    lrd.rightLeft();
                }
            }
        }
    }
}
