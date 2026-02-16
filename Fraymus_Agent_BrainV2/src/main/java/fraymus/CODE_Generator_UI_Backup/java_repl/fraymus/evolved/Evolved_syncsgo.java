package repl;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Evolved_syncsgo {

    private final Lock wg = new ReentrantLock();
    private final Condition cond = wg.newCondition();

    private final AtomicLong n = new AtomicLong(0);

    public void Go(Runnable f) {
        wg.lock();
        try {
            n.addAndGet(1);
            f.run();
            n.addAndGet(-1);
        } finally {
            wg.unlock();
            if (n.get() == 0) {
                cond.signal();
            }
        }
    }

    public long Running() {
        return n.get();
    }

    public void Wait() throws InterruptedException {
        wg.lock();
        try {
            while (n.get() == 0) {
                cond.await();
            }
        } finally {
            wg.unlock();
        }
    }
}