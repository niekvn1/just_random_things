package knickknacker.sanderserver;

import android.os.Bundle;

import knickknacker.sanderserver.TCP.Message;
import knickknacker.sanderserver.TCP.TCPServerSide;
import knickknacker.sanderserver.TCP.TCPServerUser;

import static knickknacker.sanderserver.TCP.MessageTypes.*;

/**
 * Created by Niek on 28-10-17.
 *
 * Handles the connections, using the RoomSearch, Room and RoomMember classes
 */

public class TCPListener implements TCPServerUser {
    private TCPCallback callback;
    private TCPServerSide server;

    private long next_id = 10000;

    public TCPListener(TCPCallback callback) {
        this.callback = callback;
        this.server = new TCPServerSide(this);
        this.server.startServer();
    }

    /** Handle received messages from the network. */
    public void onReceive(byte[] data, String address, int port) {
        System.out.println("ROOM: " + address + port);
        Object o = Serialize.deserialize(data);
        if (o instanceof Message) {
            Message m = (Message) o;
            Object p = m.getData();
            switch(m.getType()) {
                case REGISTER:
                    onRegister(address, port);
            }
        }
    }

    private void onRegister(String address, int port) {
        callback.onRegister(address, port);

        Bundle bundle = new Bundle();
        bundle.putLong(LONG, next_id);

        Message msg = new Message(REGISTER_RESPONSE, bundle);
        byte[] bytes = Serialize.serialize(msg);
        server.sendTo(address, port, bytes);

        callback.onRegistered(address, port, next_id);
        next_id++;
    }

    public void onConnect(String address, int port) {
        callback.onConnect(address, port);
    }

    /** Handle the disconnection of a member. */
    public void onDisconnect(String address, int port) {
        callback.onDisconnect(address, port);
    }
}
