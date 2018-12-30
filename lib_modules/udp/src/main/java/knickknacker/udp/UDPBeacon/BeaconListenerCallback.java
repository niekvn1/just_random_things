package knickknacker.udp.UDPBeacon;

public interface BeaconListenerCallback {
    void onBeaconMessage(BeaconMessage beaconMessage, String address, int port);
}
