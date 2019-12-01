package knickknacker.tcp.Wrappers.NetworkWrappers;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import knickknacker.remotefunctioncalls.Arguments;
import knickknacker.remotefunctioncalls.FunctionCall;
import knickknacker.remotefunctioncalls.FunctionCaller;
import knickknacker.serialization.Serialize;
import knickknacker.tcp.Networking.TCPServerSide;
import knickknacker.tcp.Networking.TCPServerUser;

/** A TCP server wrapper that implements remote function calls. */
public abstract class ServerWrapper extends FunctionCaller implements TCPServerUser {
    private TCPServerSide server;

    public ServerWrapper(int port, int bufferSize) {
        this.server = new TCPServerSide(this, port, bufferSize);
        this.server.startServer();
    }

    /** Execute incoming function calls. */
    protected void execute(FunctionCall call, String address, int port) {
        String func = call.getFunc();
        Arguments args = call.getArgs();
        try {
            Class<?> cls = executor.getClass();
            Method method = cls.getDeclaredMethod(func, Arguments.class, String.class,
                    int.class);
            method.invoke(executor, args, address, port);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /** Execute incoming function calls. */
    public void onReceive(byte[] bytes, String address, int port) {
        Object object = Serialize.deserialize(bytes);
        if (object instanceof FunctionCall) {
            FunctionCall call = (FunctionCall) object;
            execute(call, address, port);
        }
    }

    /** Send outgoing function calls */
    public void clientCall(String address, int port, String func, Arguments args) {
        FunctionCall c = new FunctionCall(func, args);
        server.sendTo(address, port, Serialize.serialize(c));
    }

    /** Send outgoing function calls */
    public void clientCall(String address, int port, String func, Serializable... args) {
        FunctionCall c = new FunctionCall(func, args);
        server.sendTo(address, port, Serialize.serialize(c));
    }

    /** Broadcast outgoing function calls */
    public void broadcastCall(String func, Arguments args) {
        FunctionCall c = new FunctionCall(func, args);
        server.broadcast(Serialize.serialize(c));
    }

    /** Broadcast outgoing function calls */
    public void broadcastCall(String func, Serializable... args) {
        FunctionCall c = new FunctionCall(func, args);
        this.server.broadcast(Serialize.serialize(c));
    }

    /** Close a connection with a client. */
    public void closeConnection(String address, int port) {
        server.closeConnection(address, port);
    }

    /** Close the server. */
    public void close() {
        this.server.close();
    }
}
