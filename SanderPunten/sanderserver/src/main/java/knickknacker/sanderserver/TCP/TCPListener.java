package knickknacker.sanderserver.TCP;

import knickknacker.tcp.Message;
import knickknacker.tcp.Serialize;
import knickknacker.tcp.TCPServerSide;
import knickknacker.tcp.TCPServerUser;
import knickknacker.tcp.UserData;

import static knickknacker.tcp.MessageTypes.*;

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
                case MSG_REGISTER:
                    onRegister(address, port);
            }
        }
    }

    private void onRegister(String address, int port) {
        callback.onRegister(address, port);

        UserData userData = new UserData(next_id);

        Message msg = new Message(MSG_REGISTER_RESPONSE, userData);
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
