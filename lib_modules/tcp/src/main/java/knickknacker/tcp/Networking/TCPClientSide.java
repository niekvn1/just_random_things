package knickknacker.tcp.Networking;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by Niek on 30-10-17.
 *
 * Handles the TCP connection of the client side.
 */

public class TCPClientSide {
    private final String CONNECTED_KEY = "connected";
    private final String MESSAGE_KEY = "message";
    private final String CLOSED_KEY = "closed";

    private TCPClientUser user;
    private SocketHolder socket;
    private Thread connectThread;
    private Thread receiveThread;
    private Connect connect;
    private Handler connectHandler;
    private Handler messageHandler;

    public TCPClientSide(TCPClientUser user, String server_address, int server_port) {
        this.user = user;
        this.connect = new Connect(server_address, server_port);
        this.connectHandler = new Handler(new ConnectHandler());
        this.messageHandler = new Handler(new NewMessage());
    }

    /** Start a thread to connect with a server. */
    public void connect() {
        this.connectThread = new Thread(this.connect);
        connectThread.start();
    }

    /** Send data to the server. */
    public void sendData(byte[] b) {
        if (!this.socket.isClosed()) {
            new Thread(new SendToServer(b)).start();
        }
    }

    /** Start a thread to receive data. */
    public void startReceiving() {
        if (!this.socket.isClosed()) {
            if (this.receiveThread == null) {
                this.receiveThread = new Thread(new Receiver());
                this.receiveThread.start();
            }
        }
    }

    /** Close the connection with the server. */
    public void closeConnection() {
        new Thread(new TCPClientSide.CloseConnection()).start();
    }

    private class ConnectHandler implements Handler.Callback {
        public boolean handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            boolean connected = bundle.getBoolean(CONNECTED_KEY, false);
            TCPClientSide.this.user.onConnect(connected);
            return false;
        }
    }

    /** Handler to communicate with the networking threads. */
    private class NewMessage implements Handler.Callback {
        public boolean handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            if (bundle.getBoolean(CLOSED_KEY, true)) {
                TCPClientSide.this.user.onDisconnect();
                return true;
            } else {
                byte[] data = bundle.getByteArray(MESSAGE_KEY);
                TCPClientSide.this.user.onMessage(data);
                return false;
            }
        }
    }

    /** Runnable class to connect with a server. */
    private class Connect implements Runnable {
        private String address;
        private int server_port;
        private InetAddress server_address;

        private Connect(String server_address, int server_port) {
            this.address = server_address;
            this.server_port = server_port;
        }

        @Override
        public void run() {
            try {
                server_address = InetAddress.getByName(address);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Message msg = TCPClientSide.this.connectHandler.obtainMessage();
            Bundle bundle = new Bundle();
            try {
                TCPClientSide.this.socket = new SocketHolder(new Socket(this.server_address, this.server_port));
                TCPClientSide.this.socket.setOpen();
                bundle.putBoolean(CONNECTED_KEY, true);
            } catch (IOException e) {
                e.printStackTrace();
                bundle.putBoolean(CONNECTED_KEY, false);
            }

            msg.setData(bundle);
            msg.sendToTarget();
        }
    }

    /** Runnable class to send data to the server. */
    private class SendToServer implements Runnable {
        private byte[] data;

        private SendToServer(byte[] data) {
            this.data = data;
        }

        public void run() {
            TCPClientSide.this.socket.setSending();
            try {
                OutputStream out = TCPClientSide.this.socket.getSocket().getOutputStream();
                DataOutputStream writer = new DataOutputStream(out);
                writer.write(data, 0, data.length);
            } catch (IOException e) {
                e.printStackTrace();
            }

            TCPClientSide.this.socket.unsetSending();
        }
    }

    /** Runnable class to receive data from the server. */
    private class Receiver implements Runnable {
        public void run() {
            byte[] buffer = new byte[10192];
            int read;
            Message msg;
            Bundle bundle;
            DataInputStream reader;

            try {
                reader = new DataInputStream(TCPClientSide.this.socket.getSocket().getInputStream());
                while (!Thread.currentThread().isInterrupted() && !TCPClientSide.this.socket.isClosed()) {
                    msg = TCPClientSide.this.messageHandler.obtainMessage();
                    bundle = new Bundle();
                    read = reader.read(buffer, 0, buffer.length);
                    if (read >= 1) {
                        bundle.putBoolean(CLOSED_KEY, false);
                        bundle.putByteArray(MESSAGE_KEY, buffer);
                        msg.setData(bundle);
                        msg.sendToTarget();
                    } else {
                        TCPClientSide.this.socket.close();
                        bundle.putBoolean(CLOSED_KEY, true);
                        msg.setData(bundle);
                        msg.sendToTarget();
                        Thread.currentThread().interrupt();
                    }
                }

            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }

    /** Runnable class to close the connection with a server. */
    private class CloseConnection implements Runnable {
        public void run() {
            if (TCPClientSide.this.socket != null) {
                TCPClientSide.this.socket.close();
            }
        }
    }
}
