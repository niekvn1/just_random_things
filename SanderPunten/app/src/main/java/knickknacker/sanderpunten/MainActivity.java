package knickknacker.sanderpunten;

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
import android.view.View;

import java.util.ArrayList;

import knickknacker.sanderpunten.LayoutMechanics.Objects.ButtonMenu;
import knickknacker.sanderpunten.LayoutMechanics.Objects.LayoutBox;
import knickknacker.sanderpunten.LayoutMechanics.Objects.TextBox;
import knickknacker.sanderpunten.Drawing.Properties.Colors;
import knickknacker.sanderpunten.Drawing.Tools.TextManager;
import knickknacker.sanderpunten.LayoutMechanics.LayoutManager;
import knickknacker.sanderpunten.LayoutMechanics.LayoutManagerCallback;
import knickknacker.sanderpunten.LayoutMechanics.TouchListener;
import knickknacker.sanderpunten.Services.NetworkService;

import static knickknacker.sanderpunten.Keys.BROADCAST_KEY;

public class MainActivity extends AppCompatActivity implements LayoutManagerCallback {
    private LayoutManager layoutManager;

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
        System.out.println("FIRST CREATE");

        layoutManager = new LayoutManager(this);
        layoutManager.onCreate();

        if (this.rsr != null) {
            IntentFilter filter = new IntentFilter(BROADCAST_KEY);
            registerReceiver(this.rsr, filter);
        }

        getApplicationContext().bindService(new Intent(this, NetworkService.class), this.rsc,
                Context.BIND_AUTO_CREATE);
    }

    public void surfaceCreatedCallback(LayoutBox root, TextManager textManager) {
        ButtonMenu child = new ButtonMenu(root,0.1f, 0.9f, 0.1f, 0.9f, true, true, 3);
        child.setButtonSize(100);
        child.setButtonMargin(10);
        child.setButtonColor(Colors.WHITE_TRANS);
        child.setButtonTexture(-1);

        ArrayList<LayoutBox> buttons = child.getChilderen();

        LayoutBox button = buttons.get(0);
        TextBox text = new TextBox(button, 0.1f, 0.9f, 0.1f, 0.9f, true);
        text.setText(textManager, "Test: Deze tekst slaat nergens op en heeft alleen het doel om te kijken of de lijnen onderbroken worden als de tekst langer is dan width van de TextBox", Colors.BLUE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        layoutManager.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        layoutManager.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        layoutManager.onDestroy();
        if (this.rsm != null) {
            getApplicationContext().unbindService(this.rsc);
            this.rsm = null;
        }

        unregisterReceiver(this.rsr);
    }
}
