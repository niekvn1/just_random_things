package knickknacker.sanderserver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Messenger;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import knickknacker.sanderserver.Services.NetworkService;

import static knickknacker.sanderserver.Keys.BROADCAST_KEY;

public class MainActivity extends AppCompatActivity {

    /** This is the setup for the communication with the Service which holds the functionality
     * to find devices with UDP and connect to them with TCP. */
    private Messenger rsm = null;
    private ServiceConnection rsc = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MainActivity.this.rsm = new Messenger(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            MainActivity.this.rsm = null;
        }
    };

    /** This is where the broadcast messages from the NetworkService a analysed and redirected to the
     * right function. */
    private BroadcastReceiver rsr = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (this.rsr != null) {
            IntentFilter filter = new IntentFilter(BROADCAST_KEY);
            registerReceiver(this.rsr, filter);
        }

        boolean bound = getApplicationContext().bindService(new Intent(this, NetworkService.class), this.rsc,
                Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (this.rsm != null) {
            unbindService(this.rsc);
            this.rsm = null;
        }

        unregisterReceiver(this.rsr);
    }
}
