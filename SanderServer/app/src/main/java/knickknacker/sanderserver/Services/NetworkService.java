package knickknacker.sanderserver.Services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;

import knickknacker.sanderserver.TCPCallback;
import knickknacker.sanderserver.TCPListener;

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

            }
        }
    }

    /** Send connection loss signal to activity. */
    public void connectionLoss() {

    }
}
