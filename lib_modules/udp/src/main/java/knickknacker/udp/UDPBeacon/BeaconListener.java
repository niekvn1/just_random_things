package knickknacker.udp.UDPBeacon;

import knickknacker.serialization.Serialize;
import knickknacker.udp.Broadcasting.BroadcastCallback;
import knickknacker.udp.Broadcasting.UDPBroadcastReceiver;

public class BeaconListener implements BroadcastCallback {
    private UDPBroadcastReceiver broadcastReceiver;
    private BeaconListenerCallback callback;
    private Thread receiverThread;

    public BeaconListener(BeaconListenerCallback callback, String multicast_address, int port) {
        this.callback = callback;
        broadcastReceiver = new UDPBroadcastReceiver(this, multicast_address, port);
    }

    public void start() {
        if (this.receiverThread == null) {
            this.receiverThread = broadcastReceiver.receive();
        }
    }

    public void stop() {
        if (this.receiverThread != null) {
            this.receiverThread.interrupt();
            this.receiverThread = null;
        }
    }

    public void broadcastReceived(byte[] bytes, String address, int port) {
        BeaconMessage bm = (BeaconMessage) Serialize.deserialize(bytes);
        callback.onBeaconMessage(bm, address, port);
    }
}
