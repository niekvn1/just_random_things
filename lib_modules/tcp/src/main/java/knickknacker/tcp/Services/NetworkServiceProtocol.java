package knickknacker.tcp.Services;

public abstract class NetworkServiceProtocol {
    public final static String BROADCAST_KEY = "service_broadcast";

    public final static int WHAT_FUNC = 0;
        public final static String FUNC_NAME = "service_func_name";
            public static final String ON_CONNECT = "onConnect";
            public static final String ON_DISCONNECT = "onDisconnect";
            public static final String ON_RECEIVE = "onReceive";
            public static final String ON_SEND_TO = "onSendTo";
            public static final String ON_CLOSE = "onClose";
            public static final String ON_CONNECTION_FAILED = "onConnectionFailed";
            public static final String ON_STATUS_ERROR = "onStatusError";

        public final static String FUNC_ARGS = "service_func_args";
}
