package knickknacker.tcp.Services;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import knickknacker.tcp.Networking.TCPServerSide;
import knickknacker.tcp.Networking.TCPServerUser;

import static knickknacker.tcp.Services.NetworkServiceProtocol.FUNC_ARGS;
import static knickknacker.tcp.Services.NetworkServiceProtocol.FUNC_NAME;
import static knickknacker.tcp.Services.NetworkServiceProtocol.WHAT_FUNC;
import static knickknacker.tcp.Services.ServiceTypes.BROADCAST_KEY;

/**
 * Created by Niek on 28-12-2017.
 *
 * This is a Service that is used for Networking.
 */

public class NetworkService extends Service implements TCPServerUser {
    private final HandlerIn h_in = new HandlerIn(this);
    private final Messenger m_in = new Messenger(this.h_in);
    private TCPServerSide server;
    private int port;
    private int bufferSize;

    public NetworkService(int port, int bufferSize) {
        this.port = port;
        this.bufferSize = bufferSize;
    }

    @Override
    public void onCreate() {
        this.server = new TCPServerSide(this, port, bufferSize);
        this.server.startServer();
    }

    @Override
    public void onDestroy() {
        server.close();
    }

    public void onDisconnect(String address, int port) {
        callAPI(NetworkServiceProtocol.ON_DISCONNECT, new ByteCall(address, port, null));
    }

    public void onConnect(String address, int port) {
        callAPI(NetworkServiceProtocol.ON_CONNECT, new ByteCall(address, port, null));
    }

    public void onReceive(byte[] bytes, String address, int port) {
        callAPI(NetworkServiceProtocol.ON_RECEIVE, new ByteCall(address, port, bytes));
    }

    private void onSendTo(String address, int port, byte[] bytes) {
        server.sendTo(address, port, bytes);
    }

    private void onClose(String address, int port) {
        server.closeConnection(address, port);
    }

    /** Return IBinder. */
    @Override
    public IBinder onBind (Intent i) {
        return m_in.getBinder();
    }

    /** RemoteCall Handler for messages from bound activities. */
    public static class HandlerIn extends Handler {
        private final WeakReference<NetworkService> networkService;

        public HandlerIn(NetworkService service) {
            networkService = new WeakReference<>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WHAT_FUNC:
                    Bundle bundle = msg.getData();
                    String func = bundle.getString(FUNC_NAME, "");
                    Object args = bundle.getSerializable(FUNC_ARGS);
                    NetworkService service = networkService.get();
                    service.call(func, args);
            }
        }
    }

    public void call(String func, Object args) {
        try {
//            Log.i("Calling", func + "(" + args.getClass() + ")");
            if (args instanceof ByteCall) {
                ByteCall byteCall = (ByteCall) args;
                if (byteCall.getBytes() != null) {
                    Method method = NetworkService.class.getDeclaredMethod(func, String.class, int.class, byte[].class);
                    method.invoke(this, byteCall.getAddress(), byteCall.getPort(),  byteCall.getBytes());
                } else {
                    Method method = NetworkService.class.getDeclaredMethod(func, String.class, int.class);
                    method.invoke(this, byteCall.getAddress(), byteCall.getPort());
                }
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public void callAPI(String func, Serializable args) {
        Bundle bundle = new Bundle();
        bundle.putString(FUNC_NAME, func);
        bundle.putSerializable(FUNC_ARGS, args);

        Intent intent = new Intent();
        intent.setAction(BROADCAST_KEY);
        intent.putExtras(bundle);
        sendBroadcast(intent);
    }
}
