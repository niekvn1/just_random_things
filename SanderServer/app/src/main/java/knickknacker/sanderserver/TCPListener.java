package knickknacker.sanderserver;

import knickknacker.sanderserver.TCP.Message;
import knickknacker.sanderserver.TCP.TCPServerSide;
import knickknacker.sanderserver.TCP.TCPServerUser;

/**
 * Created by Niek on 28-10-17.
 *
 * Handles the connections, using the RoomSearch, Room and RoomMember classes
 */

public class TCPListener implements TCPServerUser {
    private TCPCallback callback;
    private TCPServerSide server;

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

            }
        }
    }

    /** Handle the disconnection of a member. */
    public void onDisconnect(String address, int port) {

    }
}
