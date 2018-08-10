package knickknacker.tcp.Services;

import java.io.Serializable;

public class ByteCall implements Serializable {
    private String address;
    private int port;
    private byte[] bytes;

    public ByteCall(String address, int port, byte[] bytes) {
        this.address = address;
        this.port = port;
        this.bytes = bytes;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }
}
