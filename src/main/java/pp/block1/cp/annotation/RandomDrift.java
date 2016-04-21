package pp.block1.cp.annotation;

public class RandomDrift extends Thread {

    private Point p;

    public RandomDrift(Point p) {
        this.p = p;
    }

    public void run() {
        while (true) {
            int n = (int) (Math.random() * 10);
            p.moveX(n);
            int m = (int) (Math.random() * 10);
            p.moveY(m);
        }
    }
}