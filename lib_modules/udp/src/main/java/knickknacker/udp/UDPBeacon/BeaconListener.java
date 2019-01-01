package knickknacker.udp.UDPBeacon;

import android.os.CountDownTimer;

import knickknacker.datastructures.ConcurrentBoolean;
import knickknacker.datastructures.ConcurrentList;
import knickknacker.serialization.Serialize;
import knickknacker.udp.Broadcasting.BroadcastCallback;
import knickknacker.udp.Broadcasting.UDPBroadcastReceiver;

public class BeaconListener implements BroadcastCallback {
    private UDPBroadcastReceiver broadcastReceiver;
    private BeaconListenerCallback callback;
    private Thread receiverThread;
    private long interval;
    private long delete;
    private ConcurrentList<BeaconMessage> messages;
    private PeriodicTasks periodicTimer;
    private ConcurrentBoolean searching;

    public BeaconListener(BeaconListenerCallback callback, String multicast_address, int port,
                          long interval, long delete) {
        this.interval = interval;
        this.delete = delete;
        this.callback = callback;
        broadcastReceiver = new UDPBroadcastReceiver(this, multicast_address, port);
        messages = new ConcurrentList<>();
        searching = new ConcurrentBoolean(false);
    }

    public void start() {
        if (this.receiverThread == null) {
            searching.set(true);
            this.receiverThread = broadcastReceiver.receive();
        }

        if (periodicTimer == null) {
            periodicTimer = new PeriodicTasks();
        }
    }

    public void stop() {
        searching.set(false);
        if (this.receiverThread != null) {
            this.receiverThread.interrupt();
            this.receiverThread = null;
        }

        if (periodicTimer != null) {
            periodicTimer.cancel();
            periodicTimer = null;
        }

        messages.clear();
    }

    public void broadcastReceived(byte[] bytes, String address, int port) {
        if (!searching.value()) {
            return;
        }

        BeaconMessage bm = (BeaconMessage) Serialize.deserialize(bytes);
        if (bm == null) {
            return;
        }

        bm.setAddress(address.substring(1));
        bm.setPort(port);
        onBeaconMessage(bm);
    }

    private void onBeaconMessage(BeaconMessage bm) {
        BeaconMessage found_bm = find(bm);
        if (found_bm == null) {
            bm.updateLastSign();
            messages.add(bm);
            callback.onNewBeaconMessage(bm);
            return;
        }

        if (found_bm.changed(bm)) {
            bm.updateLastSign();
            messages.remove(found_bm);
            messages.add(bm);
            callback.onChangedBeaconMessage(found_bm, bm);
        } else {
            found_bm.updateLastSign();
        }
    }

    private BeaconMessage find(BeaconMessage bm) {
        for (BeaconMessage bm2 : messages.getCopy()) {
            if (bm2.equals(bm)) {
                return bm2;
            }
        }

        return null;
    }

    private void timeouts() {
        long now = System.currentTimeMillis();
        for (BeaconMessage bm : messages.getCopy()) {
            if ((now - bm.getLastSign()) > delete) {
                messages.remove(bm);
                callback.onOutOfTime(bm);
            }
        }
    }

    /** Call the updateConnection function on the onFinish event. */
    private class PeriodicTasks extends CountDownTimer {
        private PeriodicTasks() {
            super(interval, interval);
            this.start();
        }

        @Override
        public void onTick(long time) {

        }

        @Override
        public void onFinish() {
            timeouts();
            if (periodicTimer != null) {
                periodicTimer = new PeriodicTasks();
            }
        }
    }
}
