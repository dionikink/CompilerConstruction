package pp.block3.cp.deadlock;

/**
 * Created by Dion on 10-5-2016.
 */
public class LeftRightDeadlock {
    private final Object left = new Object();
    private final Object right = new Object();

    public void leftRight() {
        synchronized (left) {
            synchronized (right) {
                doSomething();
            }
        }
    }

    public void rightLeft() {
        synchronized (right) {
            synchronized (left) {
                doSomething();
            }
        }
    }

    public void doSomething() {
        System.out.println("I'm doing something!");
    }
}
