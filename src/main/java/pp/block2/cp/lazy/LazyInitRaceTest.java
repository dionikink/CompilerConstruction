package pp.block2.cp.lazy;

import org.junit.Test;

/**
 * Created by Dion on 2-5-2016.
 */
public class LazyInitRaceTest {
    private LazyInitRace race = new LazyInitRace();

    @Test
    public void test() {
        LazyInitRaceThread thread1 = new LazyInitRaceThread();
        LazyInitRaceThread thread2 = new LazyInitRaceThread();

        thread1.start();
        thread2.start();

        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {}

    }

    class LazyInitRaceThread extends Thread {
        public void run() {
            for (int i = 0; i < 10; i++) {
                race.getInstance();
            }
        }
    }
}
