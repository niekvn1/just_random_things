package knickknacker.sanderserver.TCP;

/**
 * Created by Niek on 28-10-17.
 *
 * Callbacks from the UDP classes to the connection handler.
 */

public interface TCPCallback {
    void onConnect(String address, int port);

    void onDisconnect(String address, int port);

    void onRegister(String address, int port);

    void onRegistered(String address, int port, long id);

}
