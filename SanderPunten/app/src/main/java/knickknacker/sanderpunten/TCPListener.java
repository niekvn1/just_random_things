package knickknacker.sanderpunten;

import java.io.IOException;
import java.net.InetAddress;

import knickknacker.sanderpunten.TCP.Message;
import knickknacker.sanderpunten.TCP.TCPClientSide;
import knickknacker.sanderpunten.TCP.TCPClientUser;

/**
 * Created by Niek on 28-10-17.
 *
 * Handles the connections, using the RoomSearch, Room and RoomMember classes
 */

public class TCPListener implements TCPClientUser {
    private TCPCallback callback;
    private TCPClientSide client;
    private final String server_address = "86.94.184.183";
    private final int server_port = 35461;

    public TCPListener(TCPCallback callback) {
        this.callback = callback;
            this.client = new TCPClientSide(this, server_address, server_port);
            this.client.connect();
    }

    public void onConnect(boolean connected) {
        if (connected) {
            System.out.println("Connection established");
        }
    }

    /** Handle received messages from the network. */
    public void onMessage(byte[] data) {
        System.out.println("Server message");
        Object o = Serialize.deserialize(data);
        if (o instanceof Message) {
            Message m = (Message) o;
            Object p = m.getData();
            switch(m.getType()) {

            }
        }
    }

    /** Handle the disconnection of a member. */
    public void onDisconnect() {

    }
}
