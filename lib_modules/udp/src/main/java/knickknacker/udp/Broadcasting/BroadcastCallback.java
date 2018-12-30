package knickknacker.udp.Broadcasting;

import java.net.DatagramPacket;

/**
 * Created by Niek on 28-10-17.
 *
 * Callbacks to the UDP user.
 */

public interface BroadcastCallback {
    void broadcastReceived(byte[] bytes, String address, int port);
}
