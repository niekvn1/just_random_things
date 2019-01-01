package knickknacker.udp.UDPBeacon;

import java.io.Serializable;

public abstract class BeaconMessage implements Serializable {
    private String address;
    private int port;
    private long lastSign;

    public BeaconMessage() {
        port = -1;
    }

    public BeaconMessage(String address, int port) {
        this.address = address;
        this.port = port;
        lastSign = 0;
    }

    public boolean equals(BeaconMessage bm) {
        return (address.equals(bm.getAddress()) && port == bm.getPort());
    }

    public abstract boolean changed(BeaconMessage bm);

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public long getLastSign() {
        return lastSign;
    }

    public void updateLastSign() {
        this.lastSign = System.currentTimeMillis();
    }
}
