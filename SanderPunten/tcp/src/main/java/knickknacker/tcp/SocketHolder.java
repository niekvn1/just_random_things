package knickknacker.tcp;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by Niek on 4-11-17.
 *
 * This class is used for the TCP sockets to create synchronization between threads
 */

public class SocketHolder {
    private static final byte CLOSED = 0;
    private static final byte OPEN = 1;
    private static final byte SENDING = 2;

    private Socket socket;
    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private Lock readLock = lock.readLock();
    private Lock writeLock = lock.writeLock();
    /** 0 --> closed, 1 --> open, 2 --> sending data. */
    private byte socketState = CLOSED;
    private Thread receive;

    /** Constructor */
    public SocketHolder(Socket socket) {
        this.socket = socket;
    }

    public Socket getSocket() {
        return this.socket;
    }

    /** Returns if the socket is closed. */
    public boolean isClosed() {
        byte state;
        readLock.lock();
        try {
            state = socketState;
        } finally {
            readLock.unlock();
        }

        if (state == CLOSED) {
            return true;
        } else {
            return false;
        }
    }

    /** Closes the socket. */
    public void close() {
        System.out.println("SocketHolder close");
        byte temp;
        writeLock.lock();
        try {
            temp = socketState;
            socketState = CLOSED;

            if (temp == OPEN) {
                try {
                    System.out.println("Actual Closing");
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } finally {
            writeLock.unlock();
        }
    }

    /** Set the state to Open */
    public void setOpen() {
        writeLock.lock();
        try {
            socketState = OPEN;
        } finally {
            writeLock.unlock();
        }
    }

    /** Set the state to Sending */
    public void setSending() {
        writeLock.lock();
        try {
            socketState = SENDING;
        } finally {
            System.out.println("Set Sending");
        }
    }

    /** Call this function if the sending is done. */
    public void unsetSending() {
        socketState = OPEN;
        writeLock.unlock();
        System.out.println("Unset Sending");
    }

    /** Get the receive thread. */
    public Thread getReceive() {
        return receive;
    }

    /** Set the receive thread */
    public void setReceive(Thread receive) {
        this.receive = receive;
    }
}
