package knickknacker.udp.UDPBeacon;

public interface BeaconListenerCallback {
    void onNewBeaconMessage(BeaconMessage bm);

    void onChangedBeaconMessage(BeaconMessage bm_old, BeaconMessage bm_new);

    void onOutOfTime(BeaconMessage bm);
}
