package pp.block2.cp.hotel;

import java.util.*;
import java.util.concurrent.locks.*;

class Hotel extends Thread {
    private int nr_rooms = 10;
    private Person[] rooms = new Person[nr_rooms];
    private List<Person> queue = new ArrayList<>();
    private Lock queueLock = new ReentrantLock();

    boolean occupied(int i) {
        return (rooms[i] != null);
    }

    int checkIn(Person p) {
        int i = 0;
        while (occupied(i)) {
            i = (i + 1) % nr_rooms;
        }
        rooms[i] = p;
        return i;
    }

    void enter(Person p) {
        queueLock.lock();
        queue.add(p);
        queueLock.unlock();
    }

    // every desk employee is a thread
    public void run() {
        while (true) {
            if (!queue.isEmpty()) {
                queueLock.lock();
                Person guest = queue.remove(0);
                queueLock.unlock();
                checkIn(guest);
            }
        }
    }
}

class Person { 
    // some appropriate query functions
}
