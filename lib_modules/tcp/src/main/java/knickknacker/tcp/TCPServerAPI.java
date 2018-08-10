package knickknacker.tcp;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Messenger;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import knickknacker.tcp.Services.ByteCall;
import knickknacker.tcp.Services.NetworkService;
import knickknacker.tcp.Services.NetworkServiceProtocol;
import knickknacker.tcp.Services.ServiceFunctions;

import static knickknacker.tcp.Services.ServiceTypes.BROADCAST_KEY;
import static knickknacker.tcp.Services.ServiceTypes.BROADCAST_TYPE;

public class TCPServerAPI {
    private final int PORT;
    private final int BUFFER_SIZE;

    private Context context;
    private Messenger rsm = null;
    private ServiceConnection rsc;
    private TCPServerAPIUser callback;

    public TCPServerAPI(Context context, TCPServerAPIUser callback, int port, int bufferSize) {
        this.context = context;
        this.callback = callback;
        PORT = port;
        BUFFER_SIZE = bufferSize;

        /** This is the setup for the communication with the Service which holds the functionality
         * to connect with TCP. */
        rsc = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                TCPServerAPI.this.rsm = new Messenger(service);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                TCPServerAPI.this.rsm = null;
            }
        };

        if (this.rsr != null) {
            IntentFilter filter = new IntentFilter(BROADCAST_KEY);
            context.registerReceiver(this.rsr, filter);
        }

        context.bindService(new Intent(context, NetworkService.class), this.rsc,
                Context.BIND_AUTO_CREATE);
    }

    private void onConnect(String address, int port) {
        Log.i("onConnect", "Address: " + address + ":" + port);
        callback.onConnect(address, port);
    }

    private void onDisconnect(String address, int port) {
        Log.i("onDisconnect", "Address: " + address + ":" + port);
        callback.onDisconnect(address, port);
    }

    private void onReceive(String address, int port, byte[] bytes) {
        Log.i("onReceive", "Address: " + address + ":" + port);
        callback.onReceive(address, port, bytes);
    }

    public void sendTo(String address, int port, byte[] bytes) {
        Log.i("sendTo", "Address: " + address + ":" + port);
        ServiceFunctions.call(rsm, NetworkServiceProtocol.ON_SEND_TO, new ByteCall(address, port, bytes));
    }

    public void close(String address, int port) {
        Log.i("close", "Address: " + address + ":" + port);
        ServiceFunctions.call(rsm, NetworkServiceProtocol.ON_CLOSE, new ByteCall(address, port, null));
    }

    /** This is where the broadcast messages from the NetworkService a analysed and redirected to the
     * right function. */
    private BroadcastReceiver rsr = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            String func = bundle.getString(NetworkServiceProtocol.FUNC_NAME, "");
            Object args = bundle.getSerializable(NetworkServiceProtocol.FUNC_ARGS);
            call(func, args);
        }
    };

    public void call(String func, Object args) {
        try {
            Log.i("Calling", func + "(" + args.getClass() + ")");
            if (args instanceof ByteCall) {
                ByteCall byteCall = (ByteCall) args;
                if (byteCall.getBytes() != null) {
                    Method method = TCPServerAPI.class.getDeclaredMethod(func, String.class, int.class, byte[].class);
                    method.invoke(this, byteCall.getAddress(), byteCall.getPort(),  byteCall.getBytes());
                } else {
                    Method method = TCPServerAPI.class.getDeclaredMethod(func, String.class, int.class);
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

    public void onResume() {

    }

    public void onPause() {

    }

    public void onDestroy() {
        if (this.rsm != null) {
            context.unbindService(this.rsc);
            this.rsm = null;
        }

        context.unregisterReceiver(this.rsr);
    }
}
