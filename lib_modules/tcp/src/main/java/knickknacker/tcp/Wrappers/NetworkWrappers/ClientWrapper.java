package knickknacker.tcp.Wrappers.NetworkWrappers;

import java.io.Serializable;

import knickknacker.remotefunctioncalls.Arguments;
import knickknacker.remotefunctioncalls.FunctionCall;
import knickknacker.remotefunctioncalls.FunctionCaller;
import knickknacker.serialization.Serialize;
import knickknacker.tcp.Networking.TCPClientSide;
import knickknacker.tcp.Networking.TCPClientUser;

/** A TCP client wrapper that implements remote function calls. */
public abstract class ClientWrapper extends FunctionCaller implements TCPClientUser {
    private TCPClientSide client;

    public void connect(String address, int port, int bufferSize) {
        client = new TCPClientSide(this, address, port, bufferSize);
        client.connect();
    }

    /** Execute incoming function calls */
    public void onReceive(byte[] bytes) {
        Object object = Serialize.deserialize(bytes);
        if (object instanceof FunctionCall) {
            execute((FunctionCall) object);
        }
    }

    /** Send outgoing function calls */
    public void serverCall(String func, Arguments args) {
        FunctionCall c = new FunctionCall(func, args);
        this.client.send(Serialize.serialize(c));
    }

    /** Send outgoing function calls */
    public void serverCall(String func, Serializable... args) {
        FunctionCall c = new FunctionCall(func, args);
        this.client.send(Serialize.serialize(c));
    }

    /** Close connection. */
    public void close() {
        client.close();
    }
}
