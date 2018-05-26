package knickknacker.sanderserver.TCP;


/**
 * Created by Niek on 30-10-17.
 *
 * Callbacks to the class that used the TCPServer.
 */

public interface TCPServerUser {
    void onReceive(byte[] data, String address, int port);

    void onDisconnect(String address, int port);
}
