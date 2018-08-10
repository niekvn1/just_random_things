package knickknacker.niekserver.Networking;

import android.content.Context;
import android.util.Log;

import java.util.HashMap;

import knickknacker.tcp.TCPServerAPI;
import knickknacker.tcp.TCPServerAPIUser;

public class WebServer implements TCPServerAPIUser {
    protected final int PORT;
    protected final int BUFFER_SIZE;

    protected Context context;
    protected TCPServerAPI tcpapi;
    protected WebServerLog log;

    public WebServer(Context context, int port, int bufferSize) {
        PORT = port;
        BUFFER_SIZE = bufferSize;
        this.context = context;
        tcpapi = new TCPServerAPI(context, this, 11223, 2048);
    }

    public void onConnect(String address, int port) {

    }

    public void onDisconnect(String address, int port) {

    }

    public void onReceive(String address, int port, byte[] data) {
        String http = new String(data);
        Request request = new Request(address, port, http);
        handleRequest(request);
    }

    protected void handleRequest(Request request) {
        logRequest(request);
    }

    protected void logRequest(Request request) {
        if (request.isValid()) {
            if (log != null) {
                log.log(request.getMethod() + " " + request.getRequest());
            }
        }
    }

    public void setLog(WebServerLog log) {
        this.log = log;
    }

    public void onResume() {

    }

    public void onPause() {

    }

    public void onDestroy() {
        if (tcpapi != null) {
            tcpapi.onDestroy();
        }
    }

    public class Request {
        private String address;
        private int port;
        private String version;
        private String request;
        private String method;
        private String host;
        private String connection;
        private String[] accept;
        private String referer;
        private String[] encoding;
        private String[] language;
        private HashMap<String, String> GET;
        private HashMap<String, String> POST;
        private boolean valid = false;

        public Request(String address, int port, String request) {
            this.address = address;
            this.port = port;

            String[] lines = request.split("\n");
            String[] split = lines[0].split(" ");
            if (split.length >= 3) {
                method = split[0].trim();
                version = split[2].trim();

                String[] getArgs = split[1].trim().split("\\?");
                this.request = getArgs[0];
                if (getArgs.length > 1) {
                    GET = new HashMap<>();
                    String[] args = getArgs[1].split("&");
                    String[] keyValue;
                    for (String arg : args) {
                        keyValue = arg.split("=");
                        if (keyValue.length == 2) {
                            GET.put(keyValue[0], keyValue[1]);
                        }
                    }
                }

                valid = true;
            }

            for (String line : lines) {
                split = line.split(":");
                if (split.length < 2) {
                    break;
                } else if (split[0].trim().equals("Host")) {
                    host = split[1].trim();
                } else if (split[0].trim().equals("Connection")) {
                    connection = split[1].trim();
                } else if (split[0].trim().equals("Accept")) {
                    accept = split[1].trim().split(",");
                    for (int i = 0; i < accept.length; i++) {
                        accept[i] = accept[i].trim();
                    }
                } else if (split[0].trim().equals("Referer")) {
                    referer = split[1].trim();
                } else if (split[0].trim().equals("Accepted-Encoding")) {
                    encoding = split[1].trim().split(",");
                    for (int i = 0; i < encoding.length; i++) {
                        encoding[i] = encoding[i].trim();
                    }
                } else if (split[0].trim().equals("Accepted-Language")) {
                    language = split[1].trim().split(",");
                    for (int i = 0; i < language.length; i++) {
                        language[i] = language[i].trim();
                    }
                }
            }
        }

        public String getVersion() {
            return version;
        }

        public String getRequest() {
            return request;
        }

        public String getMethod() {
            return method;
        }

        public String getHost() {
            return host;
        }

        public String getConnection() {
            return connection;
        }

        public String[] getAccept() {
            return accept;
        }

        public String getReferer() {
            return referer;
        }

        public String[] getEncoding() {
            return encoding;
        }

        public String[] getLanguage() {
            return language;
        }

        public boolean isValid() {
            return valid;
        }

        public String getAddress() {
            return address;
        }

        public int getPort() {
            return port;
        }

        public String GET(String key) {
            return GET.get(key);
        }

        public String POST(String key) {
            return POST.get(key);
        }
    }
}
