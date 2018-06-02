package knickknacker.sanderpunten.TCP;

import android.os.Bundle;

import knickknacker.sanderpunten.Serialize;

import static knickknacker.sanderpunten.TCP.MessageTypes.*;

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

    public void register() {
        Message msg = new Message(REGISTER, null);
        byte[] bytes = Serialize.serialize(msg);
        client.sendData(bytes);
    }

    public void onConnect(boolean connected) {
        if (connected) {
            callback.onConnect();
            client.startReceiving();
        } else {
            callback.connectionFailed();
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
                case REGISTER_RESPONSE:
                    if (p instanceof Bundle) {
                        handleRegisterResponse((Bundle) p);
                    }
                    break;

            }
        }
    }

    private void handleRegisterResponse(Bundle data) {
        callback.onRegisterResponse(data.getLong(LONG, -1));
    }

    /** Handle the disconnection of a member. */
    public void onDisconnect() {
        callback.onDisconnect();
    }
}
