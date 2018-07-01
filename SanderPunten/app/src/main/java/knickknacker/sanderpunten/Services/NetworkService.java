package knickknacker.sanderpunten.Services;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.preference.PreferenceManager;

import java.io.Serializable;

import knickknacker.sanderpunten.TCP.TCPCallback;
import knickknacker.sanderpunten.TCP.TCPListener;

import static knickknacker.sanderpunten.Services.NetworkServiceProtocol.BROADCAST_KEY;
import static knickknacker.sanderpunten.Services.NetworkServiceProtocol.FUNC_ARGS;
import static knickknacker.sanderpunten.Services.NetworkServiceProtocol.FUNC_NAME;
import static knickknacker.sanderpunten.Services.NetworkServiceProtocol.WHAT_FUNC;

/**
 * Created by Niek on 28-12-2017.
 *
 * This is a Service that is used for Networking.
 */

public class NetworkService extends Service implements TCPCallback {
    private final HandlerIn h_in = new HandlerIn();
    private final Messenger m_in = new Messenger(this.h_in);

    private TCPListener client;

    @Override
    public void onCreate() {
        this.client = new TCPListener(this, PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
    }

    @Override
    public void onDestroy() {

    }

    /** Return IBinder. */
    @Override
    public IBinder onBind (Intent i) {
        return m_in.getBinder();
    }

    /** RemoteCall Handler for messages from bound activities. */
    public class HandlerIn extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WHAT_FUNC:
                    Bundle bundle = msg.getData();
                    String func = bundle.getString(FUNC_NAME, "");
                    Object args = bundle.getSerializable(FUNC_ARGS);
                    client.call(func, args);
            }
        }
    }

    public void call(String func, Serializable args) {
        Bundle bundle = new Bundle();
        bundle.putString(FUNC_NAME, func);
        bundle.putSerializable(FUNC_ARGS, args);

        Intent intent = new Intent();
        intent.setAction(BROADCAST_KEY);
        intent.putExtras(bundle);
        sendBroadcast(intent);
    }
}
