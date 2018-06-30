package knickknacker.sanderpunten;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.Messenger;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

import knickknacker.sanderpunten.ActivityTools.Popup;
import knickknacker.sanderpunten.Rendering.LayoutMechanics.Objects.Button;
import knickknacker.sanderpunten.Rendering.LayoutMechanics.Objects.ButtonMenu;
import knickknacker.sanderpunten.Rendering.LayoutMechanics.Objects.LayoutBox;
import knickknacker.sanderpunten.Rendering.LayoutMechanics.Objects.TextBar;
import knickknacker.sanderpunten.Rendering.LayoutMechanics.Objects.TextBarCallback;
import knickknacker.sanderpunten.Rendering.LayoutMechanics.Objects.TextBox;
import knickknacker.sanderpunten.Rendering.Drawing.Properties.Colors;
import knickknacker.sanderpunten.Rendering.Drawing.Tools.TextManager;
import knickknacker.sanderpunten.Rendering.LayoutMechanics.LayoutManager;
import knickknacker.sanderpunten.Rendering.LayoutMechanics.LayoutManagerCallback;
import knickknacker.sanderpunten.Rendering.LayoutMechanics.Touch.TouchCallback;
import knickknacker.sanderpunten.Services.NetworkService;
import knickknacker.sanderpunten.Services.ServiceFunctions;
import knickknacker.sanderpunten.Storage.LocalStorage;
import knickknacker.tcp.Signables.PublicUserData;

import static knickknacker.sanderpunten.Services.ServiceTypes.BROADCAST_KEY;
import static knickknacker.sanderpunten.Services.ServiceTypes.BROADCAST_TYPE;
import static knickknacker.sanderpunten.Services.ServiceTypes.CONNECTED;
import static knickknacker.sanderpunten.Services.ServiceTypes.DISCONNECTED;
import static knickknacker.sanderpunten.Services.ServiceTypes.FAILED_TO_CONNECT;
import static knickknacker.sanderpunten.Services.ServiceTypes.OBJECT_KEY;
import static knickknacker.sanderpunten.Services.ServiceTypes.OBJECT_PUBLIC_USER_DATA;
import static knickknacker.sanderpunten.Services.ServiceTypes.REGISTER_RESPONSE;
import static knickknacker.sanderpunten.Services.ServiceTypes.WHAT_LOGIN;
import static knickknacker.sanderpunten.Services.ServiceTypes.WHAT_REGISTER;

public class MainActivity extends AppCompatActivity implements LayoutManagerCallback, TextBarCallback {
    private final byte STATE_MAIN = 0;
    private final byte STATE_PROFILE = 1;
    private final byte STATE_POPUP = 2;

    private final int LAYOUT_COUNT = 3;

    public static final String NAME_KEY = "userdata_name";
    public static final String ID_KEY = "userdata_id";

    private final String TEXTBOX_NAME = "textview_name";
    private final String TEXTBAR_NAME = "textbar_name";

    private LayoutManager layoutManager;
    private int[] menuTextureIds = {R.drawable.struissander, R.drawable.sanderstrand,
                                    R.drawable.dumb_sheep, R.drawable.multiple_sheep,
                                    R.drawable.sheep_low, R.drawable.freek,
                                    R.drawable.error_sheep};
    private byte menuState = STATE_MAIN;

    private boolean mainLoaded = false;
    private boolean profileLoaded = false;

    private LocalStorage storage = null;
    private SharedPreferences settings;

    private TextManager font30;
    private TextManager font35;
    private TextManager font40;
    private TextManager font45;

    private Popup popup;


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
                    popup("Could not establish a connection with the server.");
                    break;
                case CONNECTED:
                    Toast.makeText(context, "Connected!", Toast.LENGTH_SHORT).show();
                    onConnect();
                    break;
                case DISCONNECTED:
                    popup("Lost connection with the server, restart the app. If that doesn't work, the server is probably down.");
                    break;
                case REGISTER_RESPONSE:
                    Toast.makeText(context, "Register Response!", Toast.LENGTH_SHORT).show();
                    handleRegisterResponse((PublicUserData) bundle.getSerializable(OBJECT_KEY));
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocalStorage();

        layoutManager = new LayoutManager(this, LAYOUT_COUNT);
        layoutManager.onCreate();
        setContentView(layoutManager.getView());
    }

    private void connectToServer() {
        if (this.rsr != null) {
            IntentFilter filter = new IntentFilter(BROADCAST_KEY);
            registerReceiver(this.rsr, filter);
        }

        getApplicationContext().bindService(new Intent(this, NetworkService.class), this.rsc,
                Context.BIND_AUTO_CREATE);
    }

    private void loadLocalStorage() {
        storage = new LocalStorage();
        settings = PreferenceManager.getDefaultSharedPreferences(this);
        int id = settings.getInt(ID_KEY, -1);

        PublicUserData publicUserData = new PublicUserData(id);
        storage.setPublicUserData(publicUserData);

        if (id != - 1) {
            publicUserData.setName(settings.getString(NAME_KEY, null));
        }

        System.out.println("Loaded: " + publicUserData.getName() + " " + publicUserData.getId());
    }

    private void saveLocalStorage() {
        SharedPreferences.Editor editor = settings.edit();
        editor.apply();
        saveUserData();
    }

    private void saveUserData() {
        SharedPreferences.Editor editor = settings.edit();

        editor.putInt(ID_KEY, storage.getPublicUserData().getId());
        editor.putString(NAME_KEY, storage.getPublicUserData().getName());

        editor.apply();

        System.out.println("Saved: " + storage.getPublicUserData().getName() + " " + storage.getPublicUserData().getId());
    }

    private void onConnect() {
        if (storage.getPublicUserData().getId() == -1) {
            register();
        } else {
            login();
        }
    }

    private void register() {
        ServiceFunctions.signal(rsm, WHAT_REGISTER);
    }

    private void login() {
        ServiceFunctions.sendObject(rsm, WHAT_LOGIN, storage.getPublicUserData(), OBJECT_PUBLIC_USER_DATA);
    }

    private void handleRegisterResponse(PublicUserData publicUserData) {
        if (publicUserData == null) {
            Toast.makeText(this, "Server Registration Failed", Toast.LENGTH_SHORT).show();
        } else {
            storage.getPublicUserData().setName(publicUserData.getName());
            storage.getPublicUserData().setId(publicUserData.getId());
            storage.getPublicUserData().setSanderpunten(publicUserData.getSanderpunten());
            saveUserData();
            TextBox namebox = (TextBox) layoutManager.getDirectAccess(TEXTBOX_NAME);
            namebox.setText(publicUserData.getName());
        }
    }

    public void setupLayout(LayoutBox root) {
        font30 = new TextManager(this.getAssets());
        font30.setFontFile("font/well_bred.otf");
        font30.setSize(30);

        font35 = new TextManager(this.getAssets());
        font35.setFontFile("font/well_bred.otf");
        font35.setSize(35);

        font40 = new TextManager(this.getAssets());
        font40.setFontFile("font/well_bred.otf");
        font40.setSize(40);

        font45 = new TextManager(this.getAssets());
        font45.setFontFile("font/well_bred.otf");
        font45.setSize(45);

        mainLoaded = true;
        layoutManager.loadTextures(menuTextureIds);

        connectToServer();

        root.setBackgroundTexture(layoutManager.getTextures()[2]);
        root.setColor(Colors.WHITE);

        ButtonMenu child = new ButtonMenu(layoutManager, root,0.1f, 0.9f, 0.1f, 0.9f, true, true, 3);
        child.setChildMargin(20f);
        child.setChildColor(Colors.WHITE_ALPHA_6);
        child.setChildTexture(-1);

        ArrayList<LayoutBox> buttons = child.getChilderen();
        initButtons(buttons);
    }

    public void loadLayout(float unit) {
        font30.load(unit);
        font35.load(unit);
        font40.load(unit);
        font45.load(unit);
    }

    private void initButtons(ArrayList<LayoutBox> buttons) {
        Button button = (Button) buttons.get(0);
        button.setHitColor(Colors.RED_ALPHA_6);

        TextBox pre_profile = new TextBox(layoutManager, button, 0.1f, 0.9f, 0.66f, 0.95f, true);
        pre_profile.setTextManager(font35);
        pre_profile.setColor(Colors.BLACK_ALPHA_6);
        pre_profile.setText("PROFILE", Colors.WHITE);

        TextBox pre_name = new TextBox(layoutManager, button, 0.1f, 0.4f, 0.4f, 0.6f, true);
        pre_name.setTextManager(font35);
        pre_name.setColor(Colors.GRAY_ALPHA_6);
        pre_name.setText("Name:", Colors.WHITE);

        TextBox name = new TextBox(layoutManager, button, 0.5f, 0.9f, 0.4f, 0.6f, true);
        name.setTextManager(font35);
        name.setColor(Colors.GRAY_ALPHA_6);
        name.setText(storage.getPublicUserData().getName(), Colors.WHITE);
        layoutManager.addDirectAccess(name, TEXTBOX_NAME);

        TextBox pre_punten = new TextBox(layoutManager, button, 0.1f, 0.4f, 0.1f, 0.3f, true);
        pre_punten.setTextManager(font35);
        pre_punten.setColor(Colors.GRAY_ALPHA_6);
        pre_punten.setText("S-punten:", Colors.WHITE);

        TextBox punten = new TextBox(layoutManager, button, 0.5f, 0.9f, 0.1f, 0.3f, true);
        punten.setTextManager(font35);
        punten.setColor(Colors.GRAY_ALPHA_6);
        punten.setText(String.valueOf(storage.getPublicUserData().getSanderpunten()), Colors.WHITE);

        button.setTouchCallback(new TouchCallback() {
            @Override
            public void onTouch(LayoutBox box) {
                userProfile();
            }
        });
    }

    private void setupProfileLayout(LayoutBox root) {
        root.setBackgroundTexture(layoutManager.getTextures()[3]);
        root.setColor(Colors.WHITE);

        LayoutBox upper = new LayoutBox(layoutManager, root, 0.05f, 0.95f, 0.60f, 0.95f, true);
        LayoutBox bottom = new LayoutBox(layoutManager, root, 0.05f, 0.95f, 0.05f, 0.50f, true);
        upper.setColor(Colors.WHITE_ALPHA_6);
        bottom.setColor(Colors.WHITE_ALPHA_6);

        TextBox pre_name = new TextBox(layoutManager, bottom, 0.05f, 0.25f, 0.8f, 0.95f, true);
        pre_name.setColor(Colors.GRAY_ALPHA_6);
        pre_name.setTextManager(font35);
        pre_name.setText("Name: ", Colors.BLACK);

        TextBar name = new TextBar(layoutManager, bottom, 0.30f, 0.95f, 0.8f, 0.95f, true);
        name.setColor(Colors.GRAY_ALPHA_6);
        name.setTextManager(font35);
        name.setText(storage.getPublicUserData().getName(), Colors.BLACK);
        name.setId(TEXTBAR_NAME);
        name.setCallback(this);
    }

    private void popup(String text) {
        Log.i("POPUP", "creating popup.");
        if (popup == null) {
            popup = new Popup(layoutManager, STATE_POPUP);
        }

        popup.setText(font30, font45, text, "OK");
        layoutManager.load(STATE_POPUP, true);
    }

    public void onTextCommitted(TextBar textBar) {
        switch (textBar.getId()) {
            case TEXTBAR_NAME:
                storage.getPublicUserData().setName(textBar.getText());
                saveUserData();

                TextBox namebox = (TextBox) layoutManager.getDirectAccess(TEXTBOX_NAME);
                namebox.setText(storage.getPublicUserData().getName());
                break;
        }
    }

    private void userProfile() {
        if (menuState == STATE_MAIN) {
            if (!profileLoaded) {
                LayoutBox root = layoutManager.newLayout(STATE_PROFILE);
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
