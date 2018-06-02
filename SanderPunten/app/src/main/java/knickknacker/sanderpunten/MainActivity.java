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
import android.widget.Toast;

import java.util.ArrayList;

import knickknacker.sanderpunten.Rendering.LayoutMechanics.Objects.Button;
import knickknacker.sanderpunten.Rendering.LayoutMechanics.Objects.ButtonMenu;
import knickknacker.sanderpunten.Rendering.LayoutMechanics.Objects.LayoutBox;
import knickknacker.sanderpunten.Rendering.LayoutMechanics.Objects.TextBox;
import knickknacker.sanderpunten.Rendering.Drawing.Properties.Colors;
import knickknacker.sanderpunten.Rendering.Drawing.Tools.TextManager;
import knickknacker.sanderpunten.Rendering.LayoutMechanics.LayoutManager;
import knickknacker.sanderpunten.Rendering.LayoutMechanics.LayoutManagerCallback;
import knickknacker.sanderpunten.Rendering.LayoutMechanics.Touch.TouchCallback;
import knickknacker.sanderpunten.Services.NetworkService;
import knickknacker.sanderpunten.Services.ServiceFunctions;

import static knickknacker.sanderpunten.Services.ServiceTypes.BROADCAST_KEY;
import static knickknacker.sanderpunten.Services.ServiceTypes.BROADCAST_TYPE;
import static knickknacker.sanderpunten.Services.ServiceTypes.CONNECTED;
import static knickknacker.sanderpunten.Services.ServiceTypes.DISCONNECTED;
import static knickknacker.sanderpunten.Services.ServiceTypes.FAILED_TO_CONNECT;
import static knickknacker.sanderpunten.Services.ServiceTypes.LONG_KEY;
import static knickknacker.sanderpunten.Services.ServiceTypes.REGISTER_RESPONSE;
import static knickknacker.sanderpunten.Services.ServiceTypes.WHAT_REGISTER;

public class MainActivity extends AppCompatActivity implements LayoutManagerCallback {
    private final byte STATE_MAIN = 0;
    private final byte STATE_PROFILE = 1;

    private LayoutManager layoutManager;
    private int[] menuTextureIds = {R.drawable.struissander, R.drawable.sanderstrand};
    private byte menuState = STATE_MAIN;

    private boolean mainLoaded = false;
    private boolean profileLoaded = false;

    private UserData userData = null;


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
                case FAILED_TO_CONNECT:
                    Toast.makeText(context, "Could not establish connection.", Toast.LENGTH_LONG).show();
                    break;
                case CONNECTED:
                    Toast.makeText(context, "Connected!", Toast.LENGTH_LONG).show();
                    onConnect();
                    break;
                case DISCONNECTED:
                    Toast.makeText(context, "Disconnected!", Toast.LENGTH_LONG).show();
                    break;
                case REGISTER_RESPONSE:
                    Toast.makeText(context, "Register Response!", Toast.LENGTH_LONG).show();
                    handleRegisterResponse(bundle.getLong(LONG_KEY, -1));
                    break;
            }
        }
    };

    private void onConnect() {
        if (userData == null) {
            register();
        }
    }

    private void register() {
        ServiceFunctions.signal(rsm, WHAT_REGISTER);
    }

    private void handleRegisterResponse(long id) {
        if (id != -1) {
            userData = new UserData(id);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        layoutManager = new LayoutManager(this, 2);
        layoutManager.onCreate();
        setContentView(layoutManager.getView());

        if (this.rsr != null) {
            IntentFilter filter = new IntentFilter(BROADCAST_KEY);
            registerReceiver(this.rsr, filter);
        }

        getApplicationContext().bindService(new Intent(this, NetworkService.class), this.rsc,
                Context.BIND_AUTO_CREATE);
    }

    public void surfaceCreatedCallback(LayoutBox root) {
        mainLoaded = true;
        layoutManager.loadTextures(menuTextureIds);
        root.setBackgroundTexture(layoutManager.getTextures()[0]);
        root.setColor(Colors.WHITE);

        ButtonMenu child = new ButtonMenu(layoutManager, root,0.1f, 0.9f, 0.1f, 0.9f, true, true, 3);
        child.setChildMargin(20f);
        child.setChildColor(Colors.WHITE_TRANS);
        child.setChildTexture(-1);

        ArrayList<LayoutBox> buttons = child.getChilderen();
        initButtons(buttons);
    }

    private void initButtons(ArrayList<LayoutBox> buttons) {
        Button button = (Button) buttons.get(0);
        button.setHitColor(Colors.RED_TRANS);
        TextBox text = new TextBox(layoutManager, button, 0.1f, 0.9f, 0.1f, 0.9f, true);
        TextManager textManager = new TextManager(this.getAssets());
        textManager.setFontFile("font/well_bred.otf");
        textManager.setSize(30);
        text.setText(textManager, "Button", Colors.BLUE);

        button.setTouchCallback(new TouchCallback() {
            @Override
            public void onTouch(LayoutBox box) {
                System.out.println("Is this really working?????");
                userProfile();
            }
        });
    }

    private void setupProfileLayout(LayoutBox root) {
        root.setBackgroundTexture(layoutManager.getTextures()[1]);
        root.setColor(Colors.WHITE);

        LayoutBox upper = new LayoutBox(layoutManager, root, 0.05f, 0.95f, 0.60f, 0.95f, true);
        LayoutBox bottom = new LayoutBox(layoutManager, root, 0.05f, 0.95f, 0.05f, 0.50f, true);
        upper.setColor(Colors.WHITE_TRANS);
        bottom.setColor(Colors.WHITE_TRANS);


    }

    private void userProfile() {
        if (menuState == STATE_MAIN) {
            if (!profileLoaded) {
                LayoutBox root = layoutManager.newLayout();
                if (root != null) {
                    setupProfileLayout(root);
                    profileLoaded = true;
                }
            }

            if (profileLoaded) {
                layoutManager.switchLayout(STATE_PROFILE);
                menuState = STATE_PROFILE;
            }
        }
    }

    private void mainMenu() {
        if (menuState != STATE_MAIN) {
            if (mainLoaded) {
                layoutManager.switchLayout(STATE_MAIN);
                menuState = STATE_MAIN;
            }
        }
    }

    @Override
    public void onBackPressed() {
        switch (menuState) {
            case STATE_MAIN:
                super.onBackPressed();
                break;
            case STATE_PROFILE:
                mainMenu();
                break;
        }
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
