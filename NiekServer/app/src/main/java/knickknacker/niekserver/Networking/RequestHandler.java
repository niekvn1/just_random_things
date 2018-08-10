package knickknacker.niekserver.Networking;

import android.content.Context;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import knickknacker.assets.AssetReader;
import knickknacker.niekserver.Databasing.UserDatabase;

public class RequestHandler extends WebServer {
    private final String HTML_FOLDER = "html";
    private final String CSS_FOLDER = "css";
    private final String IMAGE_FOLDER = "images";
    private final String ICON_FOLDER = "icons";
    private final String JAVA_FOLDER = "java";

    private final String INDEX_PATH = "/html/index.html";

    private final int HEADER_INT_CODE_OK = 200;
    private final int HEADER_INT_CODE_NOT_FOUND = 404;
    private final String HEADER_STRING_CODE_OK = "Ok";
    private final String HEADER_STRING_CODE_NOT_FOUND = "Not Found";
    private final String HEADER_CONTENT_TYPE_HTML = "text/html; charset=UTF-8";
    private final String HEADER_CONTENT_TYPE_CSS = "text/css; charset=UTF-8";
    private final String HEADER_CONTENT_TYPE_JPEG = "image/jpeg";
    private final String HEADER_CONTENT_TYPE_PNG = "image/png";
    private final String HEADER_CONTENT_TYPE_ICO = "icons/x-image";

    private UserDatabase userDatabase;

    public RequestHandler(Context context, int port, int bufferSize) {
        super(context, port, bufferSize);
        userDatabase = new UserDatabase(context);

    }

    @Override
    public void handleRequest(Request request) {
        super.handleRequest(request);
        byte[] response = getRequested(request);

        tcpapi.sendTo(request.getAddress(), request.getPort(), response);
        tcpapi.close(request.getAddress(), request.getPort());
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
            } else if (path[1].equals(JAVA_FOLDER)) {

            }

            if (response != null) {
                return response;
            }
        }

        return getHeader(request.getVersion(), HEADER_INT_CODE_NOT_FOUND, HEADER_STRING_CODE_NOT_FOUND, HEADER_CONTENT_TYPE_HTML).getBytes();
    }

    private byte[] string(WebServer.Request request, String type) {
        String header;
        String content;
        String path = request.getRequest();
        if (path.equals("/")) {
            path = INDEX_PATH;
        }

        if (!path.equals("")) {
            try {
                content = AssetReader.readString(context, path.substring(1));
                header = getHeader(request.getVersion(), HEADER_INT_CODE_OK, HEADER_STRING_CODE_OK, type);
                return (header + content).getBytes();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    private byte[] image(WebServer.Request request, String type) {
        byte[] header;
        byte[] content;
        try {
            content = AssetReader.readBytes(context, request.getRequest().substring(1));
            header = getHeader(request.getVersion(), HEADER_INT_CODE_OK, HEADER_STRING_CODE_OK, type).getBytes();
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            os.write(header);
            os.write(content);
            return os.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private String getHeader(String version, int intCode, String stringCode, String type) {
        String header = ""
                + version + " " + intCode + " " + stringCode + "\n"
                + "Connection: close\n"
                + "Content-Type: " + type + "\n"
                + "\n";
        return header;
    }
}
