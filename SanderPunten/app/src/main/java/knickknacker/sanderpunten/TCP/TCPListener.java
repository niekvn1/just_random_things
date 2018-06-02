package knickknacker.sanderpunten.TCP;

import android.os.Bundle;

import knickknacker.tcp.Message;
import knickknacker.tcp.Serialize;
import knickknacker.tcp.TCPClientSide;
import knickknacker.tcp.TCPClientUser;
import knickknacker.tcp.UserData;

import static knickknacker.sanderpunten.Services.ServiceTypes.REGISTER_RESPONSE;
import static knickknacker.tcp.MessageTypes.*;

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
        Message msg = new Message(MSG_REGISTER, null);
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
                    if (p instanceof UserData) {
                        handleRegisterResponse((UserData) p);
                    }
                    break;

            }
        }
    }

    private void handleRegisterResponse(UserData userData) {
        System.out.println("Userdata: " + userData);
        callback.onRegisterResponse(userData);
    }

    /** Handle the disconnection of a member. */
    public void onDisconnect() {
        callback.onDisconnect();
    }
}
