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

public class ServiceWrapper {
    private final String BROADCAST;
    private Context context;
    private Class<?> service;
    private ServiceWrapperCallback callback;

    public ServiceWrapper(ServiceWrapperCallback callback, Context context,
                          String broadcast, Class<?> service) {
        BROADCAST = broadcast;
        this.context = context;
        this.service = service;
        this.callback = callback;

        if (this.rsr != null) {
            IntentFilter filter = new IntentFilter(BROADCAST);
            context.registerReceiver(this.rsr, filter);
        }

        boolean bound = context.bindService(new Intent(context, this.service), this.rsc,
                Context.BIND_AUTO_CREATE);
        Log.i("ServiceWrapper", "bound=" + bound);
    }


    public void send(int what, Message msg) {
        msg.what = what;
        if (rsm != null) {
            try {
                rsm.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void call(String func, Serializable... objects) {
        Arguments args = new Arguments(objects.length);
        args.push(objects);

        Message msg = Message.obtain();
        Bundle bundle = new Bundle();
        bundle.putString(ServiceProtocol.KEY_FUNC_NAME, func);
        bundle.putSerializable(ServiceProtocol.KEY_FUNC_ARGS, args);
        msg.setData(bundle);
        Log.i("ServiceWrapper", "call -> " + func);
        send(ServiceProtocol.MSG_FUNCTION_CALL, msg);
    }

    private void call_callback(String func, Arguments args) {
        try {
            Log.i("ServiceWrapper", "callback -> " + func);
            Class<?> cls = context.getClass();
            Method method = cls.getDeclaredMethod(func, Arguments.class);
            method.invoke(context, args);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private Messenger rsm = null;
    private ServiceConnection rsc = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ServiceWrapper.this.rsm = new Messenger(service);
            callback.onServiceConnected();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            ServiceWrapper.this.rsm = null;
        }
    };

    private BroadcastReceiver rsr = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            String func = bundle.getString(ServiceProtocol.KEY_FUNC_NAME, "");
            Serializable o = bundle.getSerializable(ServiceProtocol.KEY_FUNC_ARGS);
            if (o instanceof Arguments) {
                Arguments args = (Arguments) o;
                call_callback(func, args);
            }
        }
    };

    public void onDestroy() {
        if (this.rsm != null) {
            context.unbindService(this.rsc);
            this.rsm = null;
        }

        context.unregisterReceiver(this.rsr);
    }
}
