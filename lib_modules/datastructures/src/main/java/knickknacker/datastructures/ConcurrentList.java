package knickknacker.datastructures;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by Niek on 4-11-17.
 *
 * Source: https://developer.android.com/reference/java/util/concurrent/locks/ReentrantReadWriteLock.html
 * A concurrent array list.
 */

public class ConcurrentList<T> {
    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock readLock = lock.readLock();
    private final Lock writeLock = lock.writeLock();
    private final ArrayList<T> list = new ArrayList<>();

    /** Add item. */
    public void add(T item) {
        writeLock.lock();
        try {
            list.add(item);
        } finally {
            writeLock.unlock();
        }
    }

    /** Remove item */
    public void remove(T item) {
        writeLock.lock();
        try {
            list.remove(item);
        } finally {
            writeLock.unlock();
        }
    }

    /** Empty the list. */
    public void clear() {
        writeLock.lock();
        try {
            list.clear();
        } finally {
            writeLock.unlock();
        }
    }

    /** Get item at index. */
    public T get(int index) {
        T item;
        readLock.lock();
        try {
            item = list.get(index);
        } finally {
            readLock.unlock();
        }

        return item;
    }

    /** Check if the list contains an item. */
    public boolean contains(T item) {
        boolean has;
        readLock.lock();
        try {
            has = list.contains(item);
        } finally {
            readLock.unlock();
        }

        return has;
    }

    /** Get a non concurrent copy of the list. */
    public ArrayList<T> getCopy() {
        ArrayList<T> copy;
        readLock.lock();
        try {
            copy = new ArrayList<>(this.list);
        } finally {
            readLock.unlock();
        }

        return  copy;
    }

    public int size() {
        int size;
        readLock.lock();
        try {
            size = list.size();
        } finally {
            readLock.unlock();
        }

        return size;
    }
}
