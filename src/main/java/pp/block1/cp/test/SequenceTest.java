package pp.block1.cp.test;

import org.junit.Assert;
import org.junit.Test;
import pp.block1.cp.block1.UnsafeSequence;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Jens on 20-4-2016.
 *
 */
public class SequenceTest {
    private static UnsafeSequence sequence = new UnsafeSequence();

    @Test
    public void testSeq() {
        SeqThread t1 = new SeqThread();
        SeqThread t2 = new SeqThread();
        t1.start();
        t2.start();
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        t1.setFalse();
        t2.setFalse();
        ArrayList<Integer> one = t1.getNext();
        ArrayList<Integer> two = t2.getNext();
        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < one.size(); i++) {
            if (one.get(i).equals(two.get(i))) {
                Assert.fail();
            }
        }
    }


    private class SeqThread extends Thread {
        private ArrayList<Integer> next = new ArrayList<>();
        private boolean done = false;
        public void run() {
            int i = 0;
            while (!done) {
                this.next.add(i, sequence.getNext());
                i++;
            }
        }
        private void setFalse() {
            done = true;
        }
        private ArrayList<Integer> getNext() {
            return next;
        }
    }
}
