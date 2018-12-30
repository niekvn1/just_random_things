package knickknacker.services;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class WrappableService extends Service {
    protected final HandlerIn h_in = new HandlerIn();
    protected final Messenger m_in = new Messenger(this.h_in);
    protected final String BROADCAST_KEY;

    public WrappableService(String broadcast_key) {
        BROADCAST_KEY = broadcast_key;
    }

    /** Return IBinder. */
    @Override
    public IBinder onBind (Intent i) {
        return m_in.getBinder();
    }

    /** Message Handler for messages from bound activities. */
    public class HandlerIn extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ServiceProtocol.MSG_FUNCTION_CALL:
                    call(msg);
            }
        }
    }

    protected void call(Message msg) {
        Bundle bundle = msg.getData();
        String func = bundle.getString(ServiceProtocol.KEY_FUNC_NAME, "");
        Serializable o = bundle.getSerializable(ServiceProtocol.KEY_FUNC_ARGS);
        if (o instanceof Arguments) {
            Arguments args = (Arguments) o;
            try {
                Class<?> cls = this.getClass();
                Method method = cls.getDeclaredMethod(func, Arguments.class);
                method.invoke(this, args);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    protected void broadcast(String func, Serializable... objects) {
        Arguments args = new Arguments(objects.length);
        args.push(objects);

        Intent intent = new Intent();
        intent.setAction(BROADCAST_KEY);
        Bundle bundle = new Bundle();
        bundle.putString(ServiceProtocol.KEY_FUNC_NAME, func);
        bundle.putSerializable(ServiceProtocol.KEY_FUNC_ARGS, args);
        intent.putExtras(bundle);
        sendBroadcast(intent);
    }
}
