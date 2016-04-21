package pp.block1.cp.test;

import org.junit.Assert;
import org.junit.Test;
import pp.block1.cp.block1.UnsafeSequence;

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
        SeqThread t3 = new SeqThread();
        SeqThread t4 = new SeqThread();
        SeqThread t5 = new SeqThread();
        SeqThread t6 = new SeqThread();
        SeqThread t7 = new SeqThread();
        SeqThread t8 = new SeqThread();
        t1.start();
        t2.start();
        t3.start();
        t4.start();
        t5.start();
        t6.start();
        t7.start();
        t8.start();
        int one = t1.getNext();
        int two = t2.getNext();
        int three = t3.getNext();
        int four = t4.getNext();
        int five = t5.getNext();
        int six = t6.getNext();
        int seven = t7.getNext();
        int eight = t8.getNext();
        int[] ints = {one, two, three, four, five, six, seven, eight};
        for (int j = 0; j < ints.length; j++) {
            for (int i = 0; i < ints.length; i++) {
                if (ints[j] == ints[i] && j != i) {
                    System.out.println(one);
                    System.out.println(two);
                    System.out.println(three);
                    System.out.println(four);
                    System.out.println(five);
                    System.out.println(six);
                    System.out.println(seven);
                    System.out.println(eight);
                    Assert.fail("Failed");
                }
            }
        }

        t1.setFalse();
        t2.setFalse();
        t3.setFalse();
        t4.setFalse();
        t5.setFalse();
        t6.setFalse();
        t7.setFalse();
        t8.setFalse();
    }


    private class SeqThread extends Thread {
        private int next;
        private boolean done = false;
        public void run() {
            while (!done) {
                this.next = sequence.getNext();
            }
        }
        private void setFalse() {
            done = true;
        }
        private int getNext() {
            return next;
        }
    }
}
