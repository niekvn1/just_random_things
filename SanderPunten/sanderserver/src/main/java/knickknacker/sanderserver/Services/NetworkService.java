package knickknacker.sanderserver.Services;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;

import knickknacker.sanderserver.TCP.TCPCallback;
import knickknacker.sanderserver.TCP.TCPListener;

import static knickknacker.sanderserver.Services.ServiceTypes.BROADCAST_KEY;
import static knickknacker.sanderserver.Services.ServiceTypes.BROADCAST_TYPE;
import static knickknacker.sanderserver.Services.ServiceTypes.STRING_DISPLAY;
import static knickknacker.sanderserver.Services.ServiceTypes.STRING_KEY;

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
        this.con = new TCPListener(this, this);
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

            }
        }
    }

    public void stringDisplay(String text) {
        Intent intent = new Intent();
        intent.setAction(BROADCAST_KEY);
        Bundle bundle = new Bundle();
        bundle.putByte(BROADCAST_TYPE, STRING_DISPLAY);
        bundle.putString(STRING_KEY, text);
        intent.putExtras(bundle);
        sendBroadcast(intent);
    }
}
