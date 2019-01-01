package knickknacker.services;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;

import java.io.Serializable;

import knickknacker.remotefunctioncalls.Arguments;
import knickknacker.remotefunctioncalls.FunctionCall;
import knickknacker.remotefunctioncalls.FunctionCaller;

public abstract class WrappableService extends Service {
    protected final HandlerIn h_in = new HandlerIn();
    protected final Messenger m_in = new Messenger(this.h_in);
    protected final String BROADCAST_KEY;
    protected WrappableServiceCallback callback;
    protected FunctionCaller caller;

    public WrappableService(String broadcast_key, WrappableServiceCallback callback) {
        BROADCAST_KEY = broadcast_key;
        this.callback = callback;
        caller = new FunctionCaller(callback);
    }

    @Override
    public void onCreate() {
        callback.onCreate(this);
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
            call(msg);
        }
    }

    protected void call(Message msg) {
        Bundle bundle = msg.getData();
        Serializable o = bundle.getSerializable(ServiceProtocol.KEY_FUNCTION_CALL);
        if (o instanceof FunctionCall) {
            caller.execute((FunctionCall) o);
        }
    }

    public void broadcast(String func, Serializable... objects) {
        FunctionCall c = new FunctionCall(func, new Arguments(objects));

        Intent intent = new Intent();
        intent.setAction(BROADCAST_KEY);
        Bundle bundle = new Bundle();
        bundle.putSerializable(ServiceProtocol.KEY_FUNCTION_CALL, c);
        intent.putExtras(bundle);
        sendBroadcast(intent);
    }
}
