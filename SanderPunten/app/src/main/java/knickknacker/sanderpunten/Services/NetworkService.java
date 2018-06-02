package knickknacker.sanderpunten.Services;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;

import knickknacker.sanderpunten.TCP.TCPCallback;
import knickknacker.sanderpunten.TCP.TCPListener;

import static knickknacker.sanderpunten.Services.ServiceTypes.*;

/**
 * Created by Niek on 28-12-2017.
 *
 * This is a Service that is used for Networking.
 */

public class NetworkService extends Service implements TCPCallback {
    private final HandlerIn h_in = new HandlerIn();
    private final Messenger m_in = new Messenger(this.h_in);

    private TCPListener con;

    @Override
    public void onCreate() {
        this.con = new TCPListener(this);
    }

    @Override
    public void onDestroy() {

    }

    /** Return IBinder. */
    @Override
    public IBinder onBind (Intent i) {
        return m_in.getBinder();
    }

    /** Message Handler for messages from bound activities. */
    public class HandlerIn extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WHAT_REGISTER:
                    con.register();
            }
        }
    }

    private void sendBundle(Bundle bundle) {
        Intent intent = new Intent();
        intent.setAction(BROADCAST_KEY);
        intent.putExtras(bundle);
        sendBroadcast(intent);
    }

    public void connectionFailed() {
        Bundle bundle = new Bundle();
        bundle.putByte(BROADCAST_TYPE, FAILED_TO_CONNECT);
        sendBundle(bundle);
    }

    public void onConnect() {
        Bundle bundle = new Bundle();
        bundle.putByte(BROADCAST_TYPE, CONNECTED);
        sendBundle(bundle);
    }

    /** Send connection loss signal to activity. */
    public void onDisconnect() {
        Bundle bundle = new Bundle();
        bundle.putByte(BROADCAST_TYPE, DISCONNECTED);
        sendBundle(bundle);
    }

    public void onRegisterResponse(long id) {
        Bundle bundle = new Bundle();
        bundle.putByte(BROADCAST_TYPE, REGISTER_RESPONSE);
        bundle.putLong(LONG_KEY, id);
        sendBundle(bundle);
    }


}
