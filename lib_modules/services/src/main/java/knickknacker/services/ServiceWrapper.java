package knickknacker.services;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import knickknacker.remotefunctioncalls.Arguments;
import knickknacker.remotefunctioncalls.FunctionCall;
import knickknacker.remotefunctioncalls.FunctionCaller;

/** A wrapper around a given WrappableService that implements Remote Function Calls */
public class ServiceWrapper extends FunctionCaller{
    private final String BROADCAST;
    private Context context;
    private Class<?> service;

    public ServiceWrapper(ServiceWrapperCallback callback, Context context,
                          String broadcast, Class<?> service) {
        BROADCAST = broadcast;
        this.context = context;
        this.service = service;
        executor = callback;

        if (this.rsr != null) {
            IntentFilter filter = new IntentFilter(BROADCAST);
            context.registerReceiver(this.rsr, filter);
        }

        boolean bound = context.bindService(new Intent(context, this.service), this.rsc,
                Context.BIND_AUTO_CREATE);
        Log.i("ServiceWrapper", "bound=" + bound);
    }

    /** Call a function on the remote end. */
    public void call(String func, Serializable... objects) {
        Arguments args = new Arguments(objects);
        Message msg = Message.obtain();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ServiceProtocol.KEY_FUNCTION_CALL, new FunctionCall(func, args));
        msg.setData(bundle);
        Log.i("ServiceWrapper", "call -> " + func);
        if (rsm != null) {
            try {
                rsm.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /** The service communication (outgoing). */
    private Messenger rsm = null;
    private ServiceConnection rsc = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ServiceWrapper.this.rsm = new Messenger(service);
            ((ServiceWrapperCallback) executor).onServiceConnected();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            ServiceWrapper.this.rsm = null;
        }
    };

    /** The service communication (incoming) */
    private BroadcastReceiver rsr = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            Serializable o = bundle.getSerializable(ServiceProtocol.KEY_FUNCTION_CALL);
            if (o instanceof FunctionCall) {
                execute((FunctionCall) o);
            }
        }
    };

    /** Clean up the service. */
    public void onDestroy() {
        if (this.rsm != null) {
            context.unbindService(this.rsc);
            this.rsm = null;
        }

        context.unregisterReceiver(this.rsr);
    }
}
