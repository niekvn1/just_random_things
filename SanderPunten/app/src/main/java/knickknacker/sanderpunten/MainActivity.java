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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import knickknacker.sanderpunten.ActivityTools.Setups.ChatMenu;
import knickknacker.sanderpunten.ActivityTools.Setups.ChatMenuCallback;
import knickknacker.sanderpunten.ActivityTools.Setups.MainMenu;
import knickknacker.sanderpunten.ActivityTools.Setups.Popup;
import knickknacker.sanderpunten.ActivityTools.Setups.MainMenuCallback;
import knickknacker.sanderpunten.ActivityTools.Setups.ProfileMenu;
import knickknacker.sanderpunten.ActivityTools.Setups.ProfileMenuCallback;
import knickknacker.sanderpunten.ActivityTools.Setups.PuntenManager;
import knickknacker.sanderpunten.ActivityTools.Setups.PuntenManagerCallback;
import knickknacker.sanderpunten.Layouts.Layout;
import knickknacker.sanderpunten.Layouts.LayoutMechanics.LayoutManager;
import knickknacker.sanderpunten.Layouts.LayoutMechanics.LayoutManagerCallback;
import knickknacker.sanderpunten.Services.NetworkService;
import knickknacker.sanderpunten.Services.NetworkServiceProtocol;
import knickknacker.sanderpunten.Services.ServiceFunctions;
import knickknacker.sanderpunten.Storage.LocalStorage;
import knickknacker.tcp.Protocol.SanderServerProtocol;
import knickknacker.shared.Signables.PublicUserData;
import knickknacker.shared.Signables.Signable;
import knickknacker.shared.Signables.SignableObject;
import knickknacker.shared.Signables.SignableString;

public class MainActivity extends AppCompatActivity implements LayoutManagerCallback, MainMenuCallback,
        ProfileMenuCallback, ChatMenuCallback, PuntenManagerCallback {
    private final byte STATE_MAIN = 0;
    private final byte STATE_PROFILE = 1;
    private final byte STATE_POPUP = 2;
    private final byte STATE_CHAT = 3;
    private final byte STATE_PUNTEN_MANAGER = 4;

    private final int LAYOUT_COUNT = 5;

    public static final String NAME_KEY = "userdata_name";
    public static final String ID_KEY = "userdata_id";
    public static final String PUNTEN_KEY = "userdata_punten";
    public static final String ADMIN_KEY = "userdata_admin";

    private LayoutManager layoutManager;
    private int[] menuTextureIds = {R.drawable.struissander, R.drawable.sanderstrand,
                                    R.drawable.dumb_sheep, R.drawable.multiple_sheep,
                                    R.drawable.sheep_low, R.drawable.freek,
                                    R.drawable.error_sheep, R.drawable.send,
                                    R.drawable.lammetje, R.drawable.plus_button,
                                    R.drawable.minus_button};
    private byte menuState = STATE_MAIN;

    private boolean mainLoaded = false;

    private LocalStorage storage = null;
    private SharedPreferences settings;
    private boolean logIn = false;

    private MainMenu mainMenu;
    private ProfileMenu profileMenu;
    private ChatMenu chatMenu;
    private PuntenManager puntenManager;
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
            String func = bundle.getString(NetworkServiceProtocol.FUNC_NAME, "");
            Object args = bundle.getSerializable(NetworkServiceProtocol.FUNC_ARGS);
            call(func, args);
        }
    };

    public void call(String func, Object args) {
        try {
            Log.i("Calling", func);
            Method method = MainActivity.class.getDeclaredMethod(func, Object.class);
            method.invoke(this, args);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

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
            IntentFilter filter = new IntentFilter(NetworkServiceProtocol.BROADCAST_KEY);
            registerReceiver(this.rsr, filter);
        }

        getApplicationContext().bindService(new Intent(this, NetworkService.class), this.rsc,
                Context.BIND_AUTO_CREATE);
    }

    private void onConnect(Object args) {
        logIn = true;
        if (storage.getPublicUserData().getId() == -1) {
            register();
        } else {
            login();
        }
    }

    private void register() {
        ServiceFunctions.call(rsm, SanderServerProtocol.FUNC_REGISTER, null);
    }

    private void onRegisterResponse(Object args) {
        if (args instanceof PublicUserData) {
            PublicUserData publicUserData = (PublicUserData) args;
            storage.getPublicUserData().setName(publicUserData.getName());
            storage.getPublicUserData().setId(publicUserData.getId());
            storage.getPublicUserData().setSanderpunten(publicUserData.getSanderpunten());
            saveUserData();
            mainMenu.changeName(publicUserData.getName());

            login();
        }
    }

    private void onConnectionFailed(Object args) {
        logIn = false;
        popup("Could not establish a connection with the server.");
    }

    private void onDisconnect(Object args) {
        logIn = false;
        popup("Lost connection with the server, restart the app. If that doesn't work, the server is probably down.");
    }

    private void login() {
        ServiceFunctions.call(rsm, SanderServerProtocol.FUNC_LOGIN, storage.getPublicUserData());
    }

    private void onLoginResponse(Object args) {
        if (args instanceof PublicUserData) {
            PublicUserData data = (PublicUserData) args;
            long sanderpunten = data.getSanderpunten();
            storage.getPublicUserData().setSanderpunten(sanderpunten);
            storage.getPublicUserData().setAdmin(data.isAdmin());
            saveUserData();

            if (mainMenu != null) {
                mainMenu.changePunten(sanderpunten);
            }

            if (profileMenu != null) {
                profileMenu.changePunten(sanderpunten);
            }
        }
    }

    public void changedName(String name) {
        ServiceFunctions.call(rsm, SanderServerProtocol.FUNC_NAME_CHANGE, new SignableString(storage.getPublicUserData().getId(), name));
    }

    private void onNameChangeResponse(Object args) {
        if (args instanceof PublicUserData) {
            PublicUserData response = (PublicUserData) args;
            storage.getPublicUserData().setName(response.getName());
            saveUserData();

            mainMenu.changeName(storage.getPublicUserData().getName());
        } else if (args instanceof String) {
            onStatusError(args);
            profileMenu.changeName(storage.getPublicUserData().getName());
        }
    }

    public void adminApply(String password) {
        ServiceFunctions.call(rsm, SanderServerProtocol.FUNC_ADMIN_APPLY, new SignableString(storage.getPublicUserData().getId(), password));
    }

    public void onAdminApplyResponse(Object args) {
        storage.getPublicUserData().setAdmin(true);
        saveUserData();

        profileMenu.onAdminApplyResponse();
    }

    private void onStatusError(Object args) {
        if (args instanceof String) {
            popup((String) args);
        }
    }

    public void setupLayout(Layout layout) {
        mainLoaded = true;
        layoutManager.loadTextures(menuTextureIds);

        connectToServer();

        mainMenu = new MainMenu(this, layoutManager, layout);
        mainMenu.setup(storage.getPublicUserData().getName(),
                storage.getPublicUserData().getSanderpunten());

        Layout puntenLayout = layoutManager.newLayout(STATE_PUNTEN_MANAGER);
        if (puntenLayout != null) {
            puntenManager = new PuntenManager(this, layoutManager, puntenLayout, storage.getPublicUserData().getId());
            puntenManager.setup();
        }

        Layout chatLayout = layoutManager.newLayout(STATE_CHAT);
        if (chatLayout != null) {
            chatMenu = new ChatMenu(this, layoutManager, chatLayout);
            chatMenu.setup();
        }
    }

    private void popup(String text) {
        if (popup == null) {
            popup = new Popup(this, layoutManager, STATE_POPUP);
            popup.setup();
        }

        popup.setText(text, "OK");
        layoutManager.load(STATE_POPUP, true);
    }

    public void userProfile() {
        if (menuState == STATE_MAIN) {
            if (profileMenu == null) {
                Layout layout = layoutManager.newLayout(STATE_PROFILE);
                if (layout != null) {
                    profileMenu = new ProfileMenu(this, layoutManager, layout);
                    profileMenu.setup(storage.getPublicUserData().getName(),
                                      storage.getPublicUserData().getId(),
                                      storage.getPublicUserData().isAdmin(),
                                      storage.getPublicUserData().getSanderpunten());
                }
            }

            if (profileMenu != null) {
                layoutManager.switchLayout(STATE_PROFILE);
                menuState = STATE_PROFILE;
            }
        }
    }

    public void chat() {
        if (menuState == STATE_MAIN) {
            if (chatMenu == null) {
                Layout layout = layoutManager.newLayout(STATE_CHAT);
                if (layout != null) {
                    chatMenu = new ChatMenu(this, layoutManager, layout);
                    chatMenu.setup();
                }
            }

            if (chatMenu != null) {
                layoutManager.switchLayout(STATE_CHAT);
                menuState = STATE_CHAT;
                chatMenu.getLayout().reload();
            }
        }
    }

    public void puntenManager() {
        if (menuState == STATE_MAIN) {
            if (puntenManager == null) {
                Layout layout = layoutManager.newLayout(STATE_PUNTEN_MANAGER);
                if (layout != null) {
                    puntenManager = new PuntenManager(this, layoutManager, layout, storage.getPublicUserData().getId());
                    puntenManager.setup();
                }
            } else {
                getUsers();
            }

            if (layoutManager != null) {
                layoutManager.switchLayout(STATE_PUNTEN_MANAGER);
                menuState = STATE_PUNTEN_MANAGER;
            }
        }
    }

    public void onChatSend(String msg) {
        ServiceFunctions.call(rsm, SanderServerProtocol.FUNC_CHAT_SEND, new SignableString(storage.getPublicUserData().getId(), msg));
    }

    public void onChatReceive(Object args) {
        if (args instanceof String) {
            String msg = (String) args;
            chatMenu.onChatReceive(msg);
        }
    }

    public void getUsers() {
        ServiceFunctions.call(rsm, SanderServerProtocol.FUNC_GET_USERS, new Signable(storage.getPublicUserData().getId()));
    }

    public void onGetUsersResponse(Object args) {
        if (args instanceof ArrayList) {
            puntenManager.setUsers((ArrayList) args, true);
        }
    }

    public void addedSanderPunten(ArrayList<PublicUserData> data) {
        ServiceFunctions.call(rsm, SanderServerProtocol.FUNC_ADDED_SANDERPUNTEN, new SignableObject(storage.getPublicUserData().getId(), data));
    }

    public void onAddedSanderPuntenBroadcast(Object args) {
        if (args instanceof ArrayList) {
            if (puntenManager != null) {
                if (menuState == STATE_PUNTEN_MANAGER) {
                    puntenManager.setUsers((ArrayList) args, true);
                } else {
                    puntenManager.setUsers((ArrayList) args, false);
                }
            }
        }
    }

    public void addedSanderPuntenMe(long punten) {
        if (profileMenu != null) {
            profileMenu.changePunten(punten);
        }

        mainMenu.changePunten(punten);
        storage.getPublicUserData().setSanderpunten(punten);
        saveUserData();
    }

    private void mainMenu() {
        if (menuState != STATE_MAIN) {
            if (mainLoaded) {
                layoutManager.switchLayout(STATE_MAIN);
                menuState = STATE_MAIN;
            }
        }
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

        publicUserData.setSanderpunten(settings.getLong(PUNTEN_KEY, 0));
        publicUserData.setAdmin(settings.getBoolean(ADMIN_KEY, false));

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
        editor.putLong(PUNTEN_KEY, storage.getPublicUserData().getSanderpunten());
        editor.putBoolean(ADMIN_KEY, storage.getPublicUserData().isAdmin());
        editor.apply();

        System.out.println("Saved: " + storage.getPublicUserData().getName() + " " + storage.getPublicUserData().getId());
    }

    public boolean isAdmin() {
        return storage.getPublicUserData().isAdmin();
    }

    @Override
    public void onBackPressed() {
        switch (menuState) {
            case STATE_MAIN:
                super.onBackPressed();
                break;
            case STATE_PROFILE:
            case STATE_CHAT:
            case STATE_PUNTEN_MANAGER:
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
