package knickknacker.sanderpunten.TCP;

/**
 * Created by Niek on 28-10-17.
 *
 * Callbacks from the UDP classes to the connection handler.
 */

public interface TCPCallback {
    void connectionFailed();

    void onConnect();

    void onDisconnect();

    void onRegisterResponse(long id);
}
