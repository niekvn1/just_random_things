package knickknacker.tcp.Wrappers.ServiceAPI;

public interface TCPServerAPIUser {
    void onConnect(String address, int port);

    void onDisconnect(String address, int port);

    void onReceive(String address, int port, byte[] data);
}
