package knickknacker.sanderserver.Services;

import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import java.io.Serializable;

import static knickknacker.sanderserver.Services.ServiceTypes.*;

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

    /** Send a signal, message without extra data. */
    public static void signal(Messenger m, int what) {
        Message msg = Message.obtain();
        send(m, what, msg);
    }

    /** Send a string. */
    public static void sendString(Messenger m, int what, String string) {
        Message msg = Message.obtain();
        Bundle bundle = new Bundle();
        bundle.putString(STRING_KEY, string);
        msg.setData(bundle);
        send(m, what, msg);
    }

    /** Send an int. */
    public static void sendInt(Messenger m, int what, int integer) {
        Message msg = Message.obtain();
        Bundle bundle = new Bundle();
        bundle.putInt(INT_KEY, integer);
        msg.setData(bundle);
        send(m, what, msg);
    }

    /** Send a boolean. */
    public static void sendBoolean(Messenger m, int what, boolean bool) {
        Message msg = Message.obtain();
        Bundle bundle = new Bundle();
        bundle.putBoolean(BOOLEAN_KEY, bool);
        msg.setData(bundle);
        send(m, what, msg);
    }

    /** Send an object. */
    public static void sendObject(Messenger m, int what, Object o, int c) {
        if (o instanceof Serializable) {
            Message msg = Message.obtain();
            Bundle bundle = new Bundle();
            bundle.putSerializable(OBJECT_KEY, (Serializable) o);
            bundle.putInt(OBJECT_TYPE, c);
            msg.setData(bundle);
            send(m, what, msg);
        } else {
            System.err.println("Object not Serializable");
        }
    }
}
