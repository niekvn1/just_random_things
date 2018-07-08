package knickknacker.sanderpunten.TCP;

import android.content.SharedPreferences;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.util.ArrayList;

import knickknacker.RSA.RSA;
import knickknacker.sanderpunten.Services.NetworkService;
import knickknacker.sanderpunten.Services.NetworkServiceProtocol;
import knickknacker.tcp.Protocol.SanderServerProtocol;
import knickknacker.tcp.RemoteCall;
import knickknacker.tcp.Serialize;
import knickknacker.tcp.Signables.Signable;
import knickknacker.tcp.Networking.TCPClientSide;
import knickknacker.tcp.Networking.TCPClientUser;
import knickknacker.tcp.Signables.PublicUserData;
import knickknacker.tcp.Signables.SignableObject;
import knickknacker.tcp.Signables.SignableString;
import knickknacker.tcp.TimeConverter;

/**
 * Created by Niek on 28-10-17.
 *
 * Handles the connections, using the RoomSearch, Room and RoomMember classes
 */

public class TCPListener implements TCPClientUser {
    public static final String PUBLIC_KEY_KEY = "userdata_public_key";
    public static final String PRIVATE_KEY_KEY = "userdata_private_key";

    private TCPCallback callback;
    private TCPClientSide client;
    private SharedPreferences settings;
    private final String server_address = "86.94.184.183";
    private final int server_port = 35461;

    private PrivateKey privateKey;
    private PublicKey publicKey;

    public TCPListener(TCPCallback callback, SharedPreferences settings) {
        this.callback = callback;
        this.client = new TCPClientSide(this, server_address, server_port);
        this.client.connect();
        this.settings = settings;

        String privateKey = settings.getString(PRIVATE_KEY_KEY, null);
        String publicKey = settings.getString(PUBLIC_KEY_KEY, null);

        if (publicKey != null && privateKey != null) {
            try {
                this.privateKey = RSA.loadPrivateKey(privateKey);
                this.publicKey = RSA.loadPublicKey(publicKey);
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            }
        } else {
            try {
                KeyPair keys = RSA.generateKeys();
                this.privateKey = keys.getPrivate();
                this.publicKey = keys.getPublic();
                saveKeys();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveKeys() {
        /** Save the public and private keys for the client. */
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(PRIVATE_KEY_KEY, RSA.saveKey(privateKey));
        editor.putString(PUBLIC_KEY_KEY, RSA.saveKey(publicKey));
        editor.apply();
    }

    public void register(Object args) {
        /** Send a register message to the server. */
        RemoteCall call = new RemoteCall(SanderServerProtocol.FUNC_REGISTER,
                                         settings.getString(PUBLIC_KEY_KEY, null),
                                         null);
        client.sendData(call.encode());
    }

    private void onRegisterResponse(Object args) {
        /** Handle the response of the server on the register message. */
        if (args instanceof PublicUserData) {
            PublicUserData publicUserData = ((PublicUserData) args);
            Log.i("ServerResponse", "Successful register");
            callback.call(NetworkServiceProtocol.ON_REGISTER_RESPONSE, publicUserData);
        }
    }

    public void login(Object args) {
        /** Send a login message to the server. */
        if (args instanceof PublicUserData) {
            PublicUserData data = (PublicUserData) args;
            serverCall(SanderServerProtocol.FUNC_LOGIN, data);
        }
    }

    public void onLoginResponse(Object args) {
        /** Handle the response of the server on the login message. */
        if (args instanceof String) {
            String response = (String) args;
            if (response.equals(SanderServerProtocol.STATUS_OK)) {
                callback.call(NetworkServiceProtocol.ON_LOGIN_RESPONSE, null);
            }
        }
    }

    public void changedName(Object args) {
        if (args instanceof SignableString) {
            SignableString data = (SignableString) args;
            serverCall(SanderServerProtocol.FUNC_NAME_CHANGE, data);
        }
    }

    public void onNameChangeResponse(Object args) {
        if (args instanceof PublicUserData) {
            callback.call(NetworkServiceProtocol.ON_NAME_CHANGE_RESPONSE, (PublicUserData) args);
        } else if (args instanceof String) {
            callback.call(NetworkServiceProtocol.ON_NAME_CHANGE_RESPONSE, (String) args);
        }
    }

    public void onChatSend(Object args) {
        if (args instanceof SignableString) {
            serverCall(SanderServerProtocol.FUNC_CHAT_SEND, (SignableString) args);
        }
    }

    public void onChatReceive(Object args) {
        if (args instanceof String) {
            callback.call(NetworkServiceProtocol.ON_CHAT_RECEIVE, (String) args);
        }
    }

    public void onGetUsers(Object args) {
        if (args instanceof Signable) {
            serverCall(SanderServerProtocol.FUNC_GET_USERS, (Signable) args);
        }
    }

    public void onGetUsersResponse(Object args) {
        if (args instanceof ArrayList) {
            callback.call(NetworkServiceProtocol.ON_GET_USERS_RESPONSE, (ArrayList) args);
        }
    }

    public void onAddedSanderPunten(Object args) {
        if (args instanceof SignableObject) {
            serverCall(SanderServerProtocol.FUNC_ADDED_SANDERPUNTEN, (SignableObject) args);
        }
    }

    public void onAddedSanderPuntenBroadcast(Object args) {
        if (args instanceof ArrayList) {
            callback.call(NetworkServiceProtocol.ON_ADDED_SANDERPUNTEN_BROADCAST, (ArrayList) args);
        }
    }

    public void onAdminApply(Object args) {
        if (args instanceof SignableString) {
            serverCall(SanderServerProtocol.FUNC_ADMIN_APPLY, (SignableString) args);
        }
    }

    public void onAdminApplyResponse(Object args) {
        if (args instanceof String) {
            String response = (String) args;
            if (response.equals(SanderServerProtocol.STATUS_OK)) {
                callback.call(NetworkServiceProtocol.ON_ADMIN_APPLY_RESPONSE, null);
            }
        }
    }

    private void serverCall(String func, Signable signable) {
        RemoteCall call = callWithSign(func, signable);
        if (call != null) {
            client.sendData(call.encode());
        }
    }

    public void onServerException(Object args) {
        if (args instanceof String) {
            Log.e("ServerException", (String) args);
            callback.call(NetworkServiceProtocol.ON_STATUS_ERROR, (String) args);
        } else {
            Log.e("ServerException", "no specifications.");
            callback.call(NetworkServiceProtocol.ON_STATUS_ERROR, "no specifications.");
        }
    }

    private RemoteCall callWithSign(String func, Signable object) {
        object.setTimestamp(TimeConverter.getCurrentDateString());
        RemoteCall call;
        try {
            call = new RemoteCall(func, object, RSA.signObject(object, privateKey));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } catch (InvalidKeyException e) {
            e.printStackTrace();
            return null;
        } catch (SignatureException e) {
            e.printStackTrace();
            return null;
        }

        return call;
    }

    public void onConnect(boolean connected) {
        if (connected) {
            callback.call(NetworkServiceProtocol.ON_CONNECT, null);
            client.startReceiving();
        } else {
            callback.call(NetworkServiceProtocol.ON_CONNECTION_FAILED, null);
        }
    }

    /** Handle received messages from the network. */
    public void onMessage(byte[] data) {
        System.out.println("Server message");
        Object o = Serialize.deserialize(data);
        if (o instanceof RemoteCall) {
            RemoteCall m = (RemoteCall) o;
            String func = m.getFunc();
            Object args = m.getData();
            call(func, args);
        }
    }

    public void call(String func, Object args) {
        try {
            Method method = TCPListener.class.getDeclaredMethod(func, Object.class);
            method.invoke(this, args);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public void onDisconnect() {
        callback.call(NetworkServiceProtocol.ON_DISCONNECT, null);
    }
}
