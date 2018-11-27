package knickknacker.niekserver.Networking;

import android.content.Context;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import knickknacker.external_storage.ExternalStorage;
import knickknacker.niekserver.SuperUser.SuperUser;

public class RequestHandler extends WebServer {
    private static final String HTML_FOLDER = "html";
    private static final String CSS_FOLDER = "css";
    private static final String IMAGE_FOLDER = "images";
    private static final String ICON_FOLDER = "icons";
    private static final String EXECUTABLE_FOLDER = "exec";
    private static final String PY_SCRIPT_FOLDER = "py";

    private static final String SERVER_ROOT = "/data/niekserver";
    private static final String PYTHON_ENV = "/data/niekserver/exec/qpyenv.sh";
    private static final String PYTHON_EXEC = "qpython.sh";
    private static final String USER_DATABASE = "/data/niekserver/databases/user.db";
    private static final String CREATE_DATABASE = "/data/niekserver/lib/userdb.py";

    private static final String INDEX_PATH = "/html/index.html";

    private static final int HEADER_INT_CODE_OK = 200;
    private static final int HEADER_INT_CODE_NOT_FOUND = 404;
    private static final int HEADER_INT_CODE_INTERNAL_SERVER_ERROR = 500;
    private static final String HEADER_STRING_CODE_OK = "Ok";
    private static final String HEADER_STRING_CODE_NOT_FOUND = "Not Found";
    private static final String HEADER_STRING_INTERNAL_SERVER_ERROR = "Internal Server Error";
    private static final String HEADER_CONTENT_TYPE_HTML = "text/html; charset=UTF-8";
    private static final String HEADER_CONTENT_TYPE_CSS = "text/css; charset=UTF-8";
    private static final String HEADER_CONTENT_TYPE_JPEG = "image/jpeg";
    private static final String HEADER_CONTENT_TYPE_PNG = "image/png";
    private static final String HEADER_CONTENT_TYPE_ICO = "icons/x-image";

    private Session session;

    public RequestHandler(Context context, int port, int bufferSize) {
        super(context, port, bufferSize);
        session = new Session();
        File dir = new File(SERVER_ROOT + "/" + EXECUTABLE_FOLDER);
        if (dir.exists()) {
            File[] files = dir.listFiles();
            for (File file : files) {
                SuperUser.ls(file.getAbsolutePath());
                SuperUser.chmod(file.getAbsolutePath(), 777);
                SuperUser.ls(file.getAbsolutePath());
            }
        }

        File db = new File(USER_DATABASE);
        if (!db.exists()) {
            SuperUser.python(PYTHON_EXEC, PYTHON_ENV, CREATE_DATABASE);
        }
    }

    @Override
    public void handleRequest(Request request) {
        super.handleRequest(request);
        byte[] response = getRequested(request);

        tcpapi.sendTo(request.getAddress(), request.getPort(), response);
//        tcpapi.close(request.getAddress(), request.getPort());
    }

    private byte[] getRequested(WebServer.Request request) {
        Log.i("HEADER", request.getRequest());
        String[] path = request.getRequest().split("/");
        byte[] response = null;
        if (request.getRequest().equals("/") || path.length > 1) {
            if (request.getRequest().equals("/") || path[1].equals(HTML_FOLDER)) {
                response = string(request, HEADER_CONTENT_TYPE_HTML);
            } else if (path[1].equals(CSS_FOLDER)) {
                response = string(request, HEADER_CONTENT_TYPE_CSS);
            } else if (path[1].equals(ICON_FOLDER)) {
                response = image(request, HEADER_CONTENT_TYPE_ICO);
            } else if (path[1].equals(EXECUTABLE_FOLDER)) {
                response = exec(request, HEADER_CONTENT_TYPE_HTML);
            } else if (path[1].equals(PY_SCRIPT_FOLDER)) {
                response = python(request, HEADER_CONTENT_TYPE_HTML);
            }

            if (response != null) {
                return response;
            }
        }

        return getHeader(request.getVersion(), HEADER_INT_CODE_NOT_FOUND, HEADER_STRING_CODE_NOT_FOUND, HEADER_CONTENT_TYPE_HTML, 0).getBytes();
    }

    private String handlePythonReaction(String key, String content, Request request) {
        Log.i("PythonReaction", key + " --> " + content);
        switch (key) {
            case "JAVA":
                return handleJava(content, request);
        }

        return null;
    }

    private String handleJava(String content, Request request) {
        HashMap<String, String> map = argStringToHashMap(content);
        if (map.containsKey("action")) {
            return handleAction(map.get("action"), request);
        }

        return null;
    }

    private String handleAction(String action, Request request) {
        String key;
        switch (action) {
            case "login":
                key = session.add(request.POST("username"));
                return key;
            case "logout":
                key = request.COOK("session_key");
                session.remove(key);
                break;
        }

        return null;
    }

    private byte[] python(Request request, String type) {
        HashMap<String, SuperUser.StreamOutput> outputs;
        String header = "";
        String content = "";
        String key;

        String user = "";
        Session.SessionUser sessionUser = session.getUser(request.COOK("session_key"));
        if (sessionUser != null) {
            user = " \'USER={\"username\":\"" + sessionUser.getUsername()  + "\"}\'";
        }

        if (ExternalStorage.isExternalStorageWritable()) {
            outputs = SuperUser.python(PYTHON_EXEC, PYTHON_ENV, SERVER_ROOT + request.getRequest() + " " + request.argsToJSON() + user);
            if (outputs.containsKey("ERROR")) {
                content = outputs.get("ERROR").getString();
                header = getHeader(request.getVersion(), HEADER_INT_CODE_INTERNAL_SERVER_ERROR, HEADER_STRING_INTERNAL_SERVER_ERROR, type, content.length());
                Log.i("ERROR", content);
                return (header + "\r\n" + content).getBytes();
            }

            Iterator it = outputs.entrySet().iterator();
            String next;
            while (it.hasNext()) {
                Map.Entry<String, SuperUser.StreamOutput> pair = (Map.Entry) it.next();
                key = pair.getKey();
                next = pair.getValue().getString();
                if (key.equals("HTML")) {
                    content = next;
                    header = getHeader(request.getVersion(), HEADER_INT_CODE_OK, HEADER_STRING_CODE_OK,
                            type, content.length()) + header;
                } else if (key.equals("JAVA")) {
                    String reaction = handlePythonReaction(key, next, request);
                    if (reaction != null) {
                        header += "set-cookie: session_key=" + reaction + "; Domain=86.94.184.183; Path=/\n";
                    }
                }
            }


            return (header + "\r\n" + content).getBytes();
        }

        return null;
    }

    private byte[] exec(Request request, String type) {
        String header;
        String content;

        if (ExternalStorage.isExternalStorageWritable()) {
            content = SuperUser.exec(SERVER_ROOT + request.getRequest());
            header = getHeader(request.getVersion(), HEADER_INT_CODE_OK, HEADER_STRING_CODE_OK, type, content.length());
            return (header + "\r\n" + content).getBytes();
        }

        return null;
    }

    private byte[] string(WebServer.Request request, String type) {
        String header;
        String content;
        String path = request.getRequest();

        if (path.equals("/")) {
            path = INDEX_PATH;
        }

        if (ExternalStorage.isExternalStorageReadable()) {
            if (!path.equals("")) {
                try {
                    content = ExternalStorage.readString(SERVER_ROOT + path);
                    header = getHeader(request.getVersion(), HEADER_INT_CODE_OK, HEADER_STRING_CODE_OK, type, content.length());
                    return (header + "\n" + content).getBytes();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    private byte[] image(WebServer.Request request, String type) {
        byte[] header;
        byte[] content;

        try {
            content = ExternalStorage.readBytes(SERVER_ROOT + request.getRequest());
            header = (getHeader(request.getVersion(), HEADER_INT_CODE_OK, HEADER_STRING_CODE_OK, type, content.length) + "\n").getBytes();
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            os.write(header);
            os.write(content);
            return os.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private String getHeader(String version, int intCode, String stringCode, String type, int length) {
        String header = ""
                + version + " " + intCode + " " + stringCode + "\r\n"
                + "Connection: Keep-Alive\r\n"
                + "Content-Type: " + type + "\r\n"
                + "Date: " + getServerTime() + "\r\n"
                + "Content-Length: " + length + "\r\n"
                + "Server: NiekServer (Unix) Java/Python3\r\n";
        return header;
    }

    private String getServerTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormat.format(calendar.getTime());
    }
}
