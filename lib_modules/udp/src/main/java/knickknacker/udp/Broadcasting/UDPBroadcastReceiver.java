package knickknacker.udp.Broadcasting;

/**
 * Created by Niek on 24-10-17.
 *
 * UDPBroadcastReceiver:
 * This class is able to receive UDP broadcast messages as byte arrays and sends it to the
 * BroadcastCallback.
 */

import java.net.*;
import java.io.*;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class UDPBroadcastReceiver {
    private static final String BYTE_ARRAY_KEY = "byte_array";
    private static final String PACKET_ADDRESS_KEY = "packet_address";
    private static final String PACKET_PORT_KEY = "packet_port";

    private MulticastSocket mcast_socket;
    private InetAddress mcast_address;
    private int port;
    private BroadcastCallback user;
    private Handler receiveHandler;

    /** Initialize the resources. */
    public UDPBroadcastReceiver(BroadcastCallback user, String mcast_address, int port) {
        try {
            this.user = user;
            this.mcast_address = InetAddress.getByName(mcast_address);
            this.port = port;
            this.mcast_socket = new MulticastSocket(this.port);
            receiveHandler = new Handler(new BroadcastReceived());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Receive a datagram packet from the broadcast channel. */
    private DatagramPacket packet_receive() {
        byte[] buf = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        try {
            this.mcast_socket.receive(packet);
            return packet;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /** Start receiving in a parallel thread. */
    public Thread receive() {
        Receive r = new Receive();
        Thread thread = new Thread(r);
        thread.start();
        return thread;
    }

    private class BroadcastReceived implements Handler.Callback {
        public boolean handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            byte[] bytes = bundle.getByteArray(BYTE_ARRAY_KEY);
            String address = bundle.getString(PACKET_ADDRESS_KEY, "");
            int port = bundle.getInt(PACKET_PORT_KEY, 0);
            UDPBroadcastReceiver.this.user.broadcastReceived(bytes, address, port);
            return false;
        }
    }

    /** This class is able to call the receive function in parallel. */
    private class Receive implements Runnable {
        public void run() {
            DatagramPacket packet;
            Message msg;
            Bundle bundle;
            byte[] bytes;
            try {
                UDPBroadcastReceiver.this.mcast_socket.joinGroup(UDPBroadcastReceiver.this.mcast_address);
                while (!Thread.currentThread().isInterrupted()) {
                    packet = packet_receive();
                    if (packet == null) {
                        continue;
                    }

                    bytes = packet.getData();
                    msg = UDPBroadcastReceiver.this.receiveHandler.obtainMessage();
                    bundle = new Bundle();
                    bundle.putByteArray(BYTE_ARRAY_KEY, bytes);
                    bundle.putString(PACKET_ADDRESS_KEY, packet.getAddress().toString());
                    bundle.putInt(PACKET_PORT_KEY, packet.getPort());
                    msg.setData(bundle);
                    msg.sendToTarget();
                }

                UDPBroadcastReceiver.this.mcast_socket.leaveGroup(UDPBroadcastReceiver.this.mcast_address);
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }
}
