package knickknacker.sanderpunten.TCP;

import java.io.Serializable;

import knickknacker.tcp.Signables.PublicUserData;

/**
 * Created by Niek on 28-10-17.
 *
 * Callbacks from the UDP classes to the connection handler.
 */

public interface TCPCallback {
    void call(String func, Serializable args);
}
