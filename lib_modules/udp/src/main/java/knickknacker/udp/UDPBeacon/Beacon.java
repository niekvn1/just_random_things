package knickknacker.udp.UDPBeacon;

import android.os.CountDownTimer;

import knickknacker.udp.Broadcasting.UDPBroadcaster;
import knickknacker.serialization.Serialize;

public class Beacon {
    private UDPBroadcaster broadcaster;
    private BeaconTimer beaconTimer;
    private Thread broadcastThread;
    private long interval;
    private BeaconMessage beaconMessage;

    public Beacon(String multicast_address, int port) {
        broadcaster = new UDPBroadcaster(multicast_address, port);
    }

    public void start(BeaconMessage beaconMessage, long interval) {
        this.interval = interval;
        this.beaconMessage = beaconMessage;

        if (this.beaconTimer == null) {
            this.beaconTimer = new BeaconTimer();
        }
    }

    public void stop() {
        if (beaconTimer != null) {
            this.beaconTimer.cancel();
            this.broadcastThread.interrupt();
            this.beaconTimer = null;
        }
    }

    /** Start a broadcastThread with an Introduce message. */
    private void send() {
        byte[] bytes = Serialize.serialize(beaconMessage);
        if (bytes != null) {
            this.broadcastThread = this.broadcaster.broadcast(bytes);
        }
    }

    /** Send a broadcast message after time, and start again if not stopped. */
    private class BeaconTimer extends CountDownTimer {
        private BeaconTimer() {
            super(interval, interval);
            this.start();
        }

        @Override
        public void onTick(long time) {

        }

        @Override
        public void onFinish() {
            send();
            if (Beacon.this.beaconTimer != null) {
                Beacon.this.beaconTimer =
                        new BeaconTimer();
            }
        }
    }
}
