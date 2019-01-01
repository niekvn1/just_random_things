package knickknacker.datastructures;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ConcurrentBoolean {
    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock readLock = lock.readLock();
    private final Lock writeLock = lock.writeLock();
    private boolean bool;

    public ConcurrentBoolean(boolean bool) {
        this.bool = bool;
    }

    public boolean value() {
        boolean b;
        readLock.lock();
        try {
            b = bool;
        } finally {
            readLock.unlock();
        }

        return b;
    }

    public void set(boolean bool) {
        writeLock.lock();
        try {
            this.bool = bool;
        } finally {
            writeLock.unlock();
        }

    }
}
