package knickknacker.tcp.Networking;

/**
 * Created by Niek on 1-11-17.
 *
 * Callbacks to classes that use the TCPClient.
 */

public interface TCPClientUser {
    void onMessage(byte[] b);

    void onConnect(boolean connected);

    void onDisconnect();
}
