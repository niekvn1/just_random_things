package knickknacker.sanderserver.TCP;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.util.Date;

import knickknacker.RSA.RSA;
import knickknacker.sanderserver.User;
import knickknacker.tcp.Protocol.SanderServerProtocol;
import knickknacker.tcp.RemoteCall;
import knickknacker.tcp.Serialize;
import knickknacker.tcp.Networking.TCPServerSide;
import knickknacker.tcp.Networking.TCPServerUser;
import knickknacker.tcp.Signables.PublicUserData;
import knickknacker.tcp.TimeConverter;

/**
 * Created by Niek on 28-10-17.
 *
 * Handles the connections, using the RoomSearch, Room and RoomMember classes
 */

public class TCPListener implements TCPServerUser {
    private final int MAX_USERS = 1000;
    private final String USER_DIR = "userdata_storage";
    private final boolean SHOW_STORE_MESSAGES = false;
    private final int FIRST_USER_ID = 100;
    private final long MAX_TIME_DIFF = 10000;

    private TCPCallback callback;
    private TCPServerSide server;
    private User[] users = new User[MAX_USERS];
    private Context context;

    private int next_id = FIRST_USER_ID;

    public TCPListener(TCPCallback callback, Context context) {
        this.callback = callback;
        this.context = context;
        this.server = new TCPServerSide(this);
        this.server.startServer();
        if (!createFolder(USER_DIR)) {
            getUsersFromMemory();
        }
    }

    /** Handle received messages from the network. */
    public void onReceive(byte[] data, String address, int port) {
        Object o = Serialize.deserialize(data);
        if (o instanceof RemoteCall) {
            RemoteCall call = (RemoteCall) o;
            String func = call.getFunc();
            Object args = call.getData();
            if (!func.equals(SanderServerProtocol.FUNC_REGISTER)) {
                if (args instanceof PublicUserData) {

//                  Check if user exists:
                    PublicUserData userData = (PublicUserData) args;
                    User user = users[userData.getId()];
                    if (user == null) {
                        Log.i("VERIFY CALL", "User Not Found");
                        server.sendTo(address, port, SanderServerProtocol.notFound(SanderServerProtocol.FUNC_SERVER_EXCEPTION));
                        return;
                    }

//                  Check if for recent date:
                    Date date = TimeConverter.stringToDate(userData.getTimestamp());
                    Date now = TimeConverter.getCurrentDate();
                    if (Math.abs(date.getTime() - now.getTime()) > MAX_TIME_DIFF) {
                        Log.i("VERIFY CALL", "Invalid Time");
                        server.sendTo(address, port, SanderServerProtocol.invalidTime(SanderServerProtocol.FUNC_SERVER_EXCEPTION));
                        return;
                    }

//                  Checking sign:
                    try {
                        if (!RSA.verifyObject(call.getSignature(), userData, user.getPublicKey())) {
                            Log.i("VERIFY CALL", "Signature Verification Failed");
                            server.sendTo(address, port, SanderServerProtocol.verificationFailure(SanderServerProtocol.FUNC_SERVER_EXCEPTION));
                            return;
                        }
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                        server.sendTo(address, port, SanderServerProtocol.javaException(SanderServerProtocol.FUNC_SERVER_EXCEPTION, e.getClass().getCanonicalName()));
                        return;
                    } catch (InvalidKeyException e) {
                        e.printStackTrace();
                        server.sendTo(address, port, SanderServerProtocol.javaException(SanderServerProtocol.FUNC_SERVER_EXCEPTION, e.getClass().getCanonicalName()));
                        return;
                    } catch (SignatureException e) {
                        e.printStackTrace();
                        server.sendTo(address, port, SanderServerProtocol.javaException(SanderServerProtocol.FUNC_SERVER_EXCEPTION, e.getClass().getCanonicalName()));
                        return;
                    }
                } else {
                    Log.i("VERIFY CALL", "Invalid Args");
                    server.sendTo(address, port, SanderServerProtocol.invalidArgs(SanderServerProtocol.FUNC_SERVER_EXCEPTION));
                    return;
                }
            }

            call(func, args, address, port);
        } else {
            server.sendTo(address, port, SanderServerProtocol.badRequest(SanderServerProtocol.FUNC_SERVER_EXCEPTION));
        }
    }

    private void call(String func, Object args, String address, int port) {
        Log.i("CALL", "calling " + func);
        try {
            Method method = TCPListener.class.getDeclaredMethod(func, Object.class, String.class, int.class);
            method.invoke(this, args, address, port);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            server.sendTo(address, port, SanderServerProtocol.javaException(SanderServerProtocol.FUNC_SERVER_EXCEPTION, e.getClass().getCanonicalName()));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            server.sendTo(address, port, SanderServerProtocol.javaException(SanderServerProtocol.FUNC_SERVER_EXCEPTION, e.getClass().getCanonicalName()));
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            server.sendTo(address, port, SanderServerProtocol.javaException(SanderServerProtocol.FUNC_SERVER_EXCEPTION, e.getClass().getCanonicalName()));
        }
    }

    private void onRegister(Object args, String address, int port) {
        callback.stringDisplay("[Register Request: " + address + ":" + port + "]");
        if (!(args instanceof String)) {
            return;
        }

        try {
            PublicKey publicKey = RSA.loadPublicKey((String) args);
            PublicUserData publicUserData = new PublicUserData(next_id);
            publicUserData.setName("user#" + next_id);
            publicUserData.setSanderpunten(0);

            User user = new User(publicUserData, publicKey);
            user.setAddress(address);
            user.setPort(port);
            user.setVerifiedLogin(true);
            users[next_id] = user;
            writeUser(next_id);

            callWithObject(SanderServerProtocol.FUNC_REGISTER_RESPONSE, publicUserData, address, port);

            callback.stringDisplay("[Registered: " + address + ":" + port + "] id: " + next_id);
            next_id++;
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            server.sendTo(address, port, SanderServerProtocol.javaException(SanderServerProtocol.FUNC_SERVER_EXCEPTION, e.getClass().getCanonicalName()));
        }
    }

    private void onLogin(Object args, String address, int port) {
        if (args instanceof PublicUserData) {
            PublicUserData data = (PublicUserData) args;
            User user = users[data.getId()];
            user.setVerifiedLogin(true);
            user.setAddress(address);
            user.setPort(port);

            server.sendTo(address, port, SanderServerProtocol.ok(SanderServerProtocol.FUNC_LOGIN_RESPONSE));

            callback.stringDisplay(user.getPublicUserData().getName() + " login verified.");
        }
    }

    private void callWithObject(String func, Serializable object, String address, int port) {
        RemoteCall call = new RemoteCall(func, object, null);
        byte[] bytes = Serialize.serialize(call);
        server.sendTo(address, port, bytes);
    }

    public void onConnect(String address, int port) {
        callback.stringDisplay("[Connection: " + address + ":" + port + "]");
    }

    /** Handle the disconnection of a member. */
    public void onDisconnect(String address, int port) {
        int id = findUserIdWithIp(address, port);
        User user;
        if (id != -1) {
            user = users[id];
            user.setVerifiedLogin(false);
            user.setAddress("offline");
            user.setPort(-1);
            callback.stringDisplay("[Disconnect: " + address + ":" + port + "] id: " + id  + " name: " + user.getPublicUserData().getName());
        } else {
            callback.stringDisplay("[Disconnect: " + address + ":" + port + "]");
        }
    }

    private int findUserIdWithIp(String address, int port) {
        for (int i = 0; i < FIRST_USER_ID; i++) {
            if (users[i] == null) {
                break;
            }

            if (users[i].getAddress().equals(address) && users[i].getPort() == port) {
                return i;
            }
        }

        for (int i = FIRST_USER_ID; i < MAX_USERS; i++) {
            if (users[i] == null) {
                return -1;
            }

            if (users[i].getAddress().equals(address) && users[i].getPort() == port) {
                return i;
            }
        }

        return -1;
    }

    private boolean createFolder(String folderName) {
        File dir = new File(context.getFilesDir(), folderName);
        if(!dir.exists()){
            dir.mkdir();
            return true;
        } else {
            return false;
        }
    }

    public void getUsersFromMemory() {
        boolean admin = true;
        for (int i = 0; i < MAX_USERS; i++) {
            if (!readUser(i)) {
                if (admin) {
                    i = FIRST_USER_ID - 1;
                    admin = false;
                } else {
                    next_id = i;
                    return;
                }
            }
        }
    }

    private boolean readUser(int index) {
        File file = new File(context.getFilesDir() + "/" + USER_DIR, index + ".txt");
        if (!file.isFile()) {
            return false;
        }

        int id;
        int punten;
        String name;
        PublicKey publicKey;
        try {
            InputStream in = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            id = Integer.parseInt(reader.readLine());
            name = reader.readLine();
            punten = Integer.parseInt(reader.readLine());

            String key = reader.readLine().replace('#', '\n');

            publicKey = RSA.loadPublicKey(key);
            reader.close();

            PublicUserData data = new PublicUserData(id);
            data.setName(name);
            data.setSanderpunten(punten);
            User user = new User(data, publicKey);
            user.setAddress("offline");
            user.setPort(-1);

            users[id] = user;

            if (SHOW_STORE_MESSAGES) {
                Log.i("FILE CHECK", "Read file (" + context.getFilesDir() + "/" + USER_DIR + "/" + index + ".txt" + "). Returning true\n"
                        + "Read: \n"
                        + id + "\n"
                        + name + "\n"
                        + punten + "\n"
                        + RSA.saveKey(publicKey).replace('#', '\n'));
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private void writeUser(int index) {
        User user = users[index];
        if (user == null) {
            return;
        }

        File file = new File(context.getFilesDir() + "/" + USER_DIR ,user.getPublicUserData().getId() + ".txt");
        try {
            if(!file.isFile()){
                file.createNewFile();
            }

            FileWriter writer = new FileWriter(file);

            String content = user.getPublicUserData().getId() + "\n"
                    + user.getPublicUserData().getName() + "\n"
                    + user.getPublicUserData().getSanderpunten() + "\n"
                    + RSA.saveKey(user.getPublicKey()).replace('\n', '#');
            writer.write(content);
            writer.flush();
            writer.close();

            if (SHOW_STORE_MESSAGES) {
                callback.stringDisplay("WROTE:\n" + content);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
