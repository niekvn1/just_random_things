package knickknacker.udp.Broadcasting;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by Niek on 3-11-17.
 *
 * UDPBroadcaster:
 * This class sends UDP broadcast messages to the given address and port.
 */

public class UDPBroadcaster {
    private DatagramSocket socket;
    private InetAddress mcast_address;
    private int port;

    /** Initialize the resources. */
    public UDPBroadcaster(String mcast_address, int port) {
        try {
            this.mcast_address = InetAddress.getByName(mcast_address);
            this.port = port;

            this.socket = new DatagramSocket();
            this.socket.setReuseAddress(true);
            this.socket.setBroadcast(true);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    /** Send a broadcast message. */
    private void byte_broadcast(byte[] b) {
        DatagramPacket packet = new DatagramPacket(b, b.length);
        packet.setAddress(this.mcast_address);
        packet.setPort(this.port);
        try {
            this.socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Start broadcasting the byte array on a parallel thread. */
    public Thread broadcast(byte[] bytes) {
        Broadcast b = new Broadcast(bytes);
        Thread thread = new Thread(b);
        thread.start();
        return thread;
    }

    /** This class is able to call the broadcast function in parallel. */
    private class Broadcast implements Runnable {
        private byte[] bytes;

        private Broadcast(byte[] bytes) {
            this.bytes = bytes;
        }

        public void run() {
            byte_broadcast(bytes);
        }
    }
}
