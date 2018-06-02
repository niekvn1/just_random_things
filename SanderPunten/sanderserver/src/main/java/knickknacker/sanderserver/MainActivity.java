package knickknacker.sanderserver;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Messenger;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;
import android.widget.TextView;

import knickknacker.sanderserver.Services.NetworkService;

import static knickknacker.sanderserver.Services.ServiceTypes.BROADCAST_KEY;
import static knickknacker.sanderserver.Services.ServiceTypes.BROADCAST_TYPE;
import static knickknacker.sanderserver.Services.ServiceTypes.STRING_DISPLAY;
import static knickknacker.sanderserver.Services.ServiceTypes.STRING_KEY;

public class MainActivity extends AppCompatActivity {
    private LinearLayout messageContainer;

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
            byte type = bundle.getByte(BROADCAST_TYPE);
            switch (type) {
                case STRING_DISPLAY:
                    displayString(bundle.getString(STRING_KEY));
                    break;
            }
        }
    };

    private void displayString(String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        messageContainer.addView(textView);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        messageContainer = findViewById(R.id.message_container);

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
