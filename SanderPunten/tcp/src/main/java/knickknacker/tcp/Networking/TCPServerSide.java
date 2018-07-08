package knickknacker.tcp.Networking;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by Niek on 30-10-17.
 *
 * Handles the TCP connection on the server side.
 */

public class TCPServerSide {
    private static final String ADDRESS = "localhost";
    private static final int PORT = 35461;
    private static final int BACKLOG = 4;
    private static final String MESSAGE_KEY = "message";
    private static final String SOCKET_ADDRESS_KEY = "socket_address";
    private static final String SOCKET_PORT_KEY = "socket_port";
    private static final String CLOSED_KEY = "socket_closed";

    private ServerSocket server;
    private TCPServerUser user;
    private Handler connectHandler;
    private Handler handler;
    private StartServer startServer;
    private Thread startThread;
    private ConcurrentList<SocketHolder> sockets;

    public TCPServerSide(TCPServerUser s) {
        this.user = s;
        this.handler = new Handler(new NewMessage());
        this.connectHandler = new Handler(new OnConnect());
        this.startServer = new StartServer();
        this.sockets = new ConcurrentList<>();
    }

    /** Start the server, and wait for connections. */
    public void startServer() {
        this.startThread = new Thread(this.startServer);
        this.startThread.start();
    }

    /** Close the server. */
    public void closeServer() {
        if (this.startThread != null) {
            this.startThread.interrupt();
        }

        new Thread(new DisconnectAll()).start();
    }

    /** Send data to address:port */
    public void sendTo(String address, int port, byte[] data) {
        Thread sendThread = new Thread(new SendTo(address, port, data));
        sendThread.start();
    }

    /** Broadcast data to all connections. */
    public void broadcast(byte[] data) {
        Thread broadcastThread = new Thread(new BroadCast(data));
        broadcastThread.start();
    }

    /** Find the socket with address and port. */
    private SocketHolder findSocket(String address, int port) {
        ArrayList<SocketHolder> list_copy = this.sockets.getCopy();
        for(SocketHolder socketHolder : list_copy) {
            if (socketHolder.getSocket().getInetAddress().toString().equals(address)
                    && socketHolder.getSocket().getPort() == port) {
                return socketHolder;
            }
        }

        return null;
    }

    /** Close the connection with address:port. */
    public void closeConnection(String address, int port) {
        new Thread(new CloseConnection(address, port)).start();
    }

    private class OnConnect implements Handler.Callback {
        public boolean handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            String address = bundle.getString(SOCKET_ADDRESS_KEY);
            int port = bundle.getInt(SOCKET_PORT_KEY);
            user.onConnect(address, port);
            return false;
        }
    }

    private class NewMessage implements Handler.Callback {
        public boolean handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            String address = bundle.getString(SOCKET_ADDRESS_KEY);
            int port = bundle.getInt(SOCKET_PORT_KEY);
            if (bundle.getBoolean(CLOSED_KEY)) {
                TCPServerSide.this.user.onDisconnect(address, port);
            } else {
                byte[] b = bundle.getByteArray(MESSAGE_KEY);
                System.out.println("Got message:" + b.length);
                TCPServerSide.this.user.onReceive(b, address, port);
            }

            return false;
        }
    }

    /** Get the address of the server. */
    public String getAddress() {
        return this.server.getLocalSocketAddress().toString();
    }

    /** Get the port of the server. */
    public int getPort() {
        return this.server.getLocalPort();
    }

    /** Runnable class to start the server. */
    private class StartServer implements Runnable {
        private InetSocketAddress isa;

        @Override
        public void run() {
            try {
//                this.isa = new InetSocketAddress(PORT);
                TCPServerSide.this.server = new ServerSocket(PORT);
//                TCPServerSide.this.server.bind(this.isa, BACKLOG);
                TCPServerSide.this.server.setReuseAddress(true);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            Message msg;
            Bundle bundle;
            Socket socket;
            SocketHolder socketHolder;
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    System.out.println("Waiting for connections on: " + server.getInetAddress().toString());
                    socket = TCPServerSide.this.server.accept();
                    System.out.println("Request");
                    socketHolder = new SocketHolder(socket);
                    socketHolder.setOpen();
                    TCPServerSide.this.sockets.add(socketHolder);

                    new Thread(new Accepted(socketHolder)).start();
                    msg = TCPServerSide.this.connectHandler.obtainMessage();
                    bundle = new Bundle();
                    bundle.putString(SOCKET_ADDRESS_KEY, socketHolder.getSocket().getInetAddress().toString());
                    bundle.putInt(SOCKET_PORT_KEY, socketHolder.getSocket().getPort());
                    msg.setData(bundle);
                    msg.sendToTarget();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            try {
                TCPServerSide.this.server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /** Runnable class to wait for messages from accepted connections. */
    private class Accepted implements Runnable {
        private SocketHolder socketHolder;

        private Accepted(SocketHolder socketHolder) {
            this.socketHolder = socketHolder;
        }

        @Override
        public void run() {
            byte[] buffer = new byte[10192];
            int read;
            Message msg;
            Bundle bundle;
            this.socketHolder.setReceive(Thread.currentThread());
            DataInputStream reader;
            try {
                reader = new DataInputStream(this.socketHolder.getSocket().getInputStream());
                while (!Thread.currentThread().isInterrupted() && !this.socketHolder.isClosed()) {
                    msg = TCPServerSide.this.handler.obtainMessage();
                    bundle = new Bundle();
                    read = reader.read(buffer, 0, buffer.length);
                    bundle.putString(SOCKET_ADDRESS_KEY, this.socketHolder.getSocket().getInetAddress().toString());
                    bundle.putInt(SOCKET_PORT_KEY, this.socketHolder.getSocket().getPort());
                    if (read >= 1) {
                        bundle.putByteArray(MESSAGE_KEY, buffer);
                        msg.setData(bundle);
                    } else {
                        this.socketHolder.close();
                        TCPServerSide.this.sockets.remove(this.socketHolder);
                        bundle.putBoolean(CLOSED_KEY, true);
                        msg.setData(bundle);
                        Thread.currentThread().interrupt();
                    }

                    msg.sendToTarget();
                }
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }

    /** Runnable class to send messages to accepted connections. */
    private class SendTo implements Runnable {
        private byte[] data;
        private String address;
        private int port;

        private SendTo(String address, int port, byte[] data) {
            this.data = data;
            this.address = address;
            this.port = port;
        }

        public void run() {
            SocketHolder socketHolder = findSocket(this.address, this.port);
            if (socketHolder != null && !socketHolder.isClosed()) {
                socketHolder.setSending();
                try {
                    OutputStream out = socketHolder.getSocket().getOutputStream();
                    DataOutputStream writer = new DataOutputStream(out);
                    Log.i("TCPServerSide", "Sending " + data.length + " bytes.");
                    writer.write(data, 0, data.length);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                socketHolder.unsetSending();
            }
        }
    }

    /** Runnable class to broadcast a message to all accepted connections. */
    private class BroadCast implements Runnable {
        private byte[] data;

        private BroadCast(byte[] data) {
            this.data = data;
        }

        public void run() {
            for (SocketHolder socketHolder : TCPServerSide.this.sockets.getCopy()) {
                if (socketHolder != null && !socketHolder.isClosed()) {
                    socketHolder.setSending();
                    try {
                        OutputStream out = socketHolder.getSocket().getOutputStream();
                        DataOutputStream writer = new DataOutputStream(out);
                        Log.i("TCPServerSide", "Broadcasting " + data.length + " bytes.");
                        writer.write(data, 0, data.length);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    socketHolder.unsetSending();
                }
            }

            System.out.println("Broadcast Sent");
        }
    }

    /** Runnable class to disconnect with all connections. */
    private class DisconnectAll implements Runnable {
        public void run() {
            System.out.println("Closing Connections");
            try {
                TCPServerSide.this.server.close();
            } catch(IOException e) {
                e.printStackTrace();
            }
            for (SocketHolder holder : TCPServerSide.this.sockets.getCopy()) {
                System.out.println("Closed");
                holder.close();
            }

            TCPServerSide.this.sockets.clear();
        }
    }

    /** Runnable class to disconnect from address:port. */
    private class CloseConnection implements Runnable {
        private String address;
        private int port;

        private CloseConnection(String address, int port) {
            this.address = address;
            this.port = port;
        }

        public void run() {
            SocketHolder socketHolder = findSocket(this.address, this.port);
            if (socketHolder != null) {
                socketHolder.getReceive().interrupt();
                socketHolder.close();
                TCPServerSide.this.sockets.remove(socketHolder);
            }
        }
    }
}
