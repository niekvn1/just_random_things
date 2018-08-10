package knickknacker.tcp.Services;

import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import java.io.Serializable;

import static knickknacker.tcp.Services.NetworkServiceProtocol.FUNC_ARGS;
import static knickknacker.tcp.Services.NetworkServiceProtocol.FUNC_NAME;
import static knickknacker.tcp.Services.NetworkServiceProtocol.WHAT_FUNC;

/**
 * Created by Niek on 30-12-2017.
 *
 * This file contains function to communicate with the RoomService.
 */

public abstract class ServiceFunctions {
    /** Send a custom message. */
    public static void send(Messenger m, int what, Message msg) {
        msg.what = what;
        if (m != null) {
            try {
                m.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public static void call(Messenger m, String func, Serializable args) {
        Message msg = Message.obtain();
        Bundle bundle = new Bundle();
        bundle.putString(FUNC_NAME, func);
        bundle.putSerializable(FUNC_ARGS, args);
        msg.setData(bundle);
        send(m, WHAT_FUNC, msg);
    }
}
