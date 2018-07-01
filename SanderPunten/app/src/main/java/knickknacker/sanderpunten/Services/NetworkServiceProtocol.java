package knickknacker.sanderpunten.Services;

public abstract class NetworkServiceProtocol {
    public final static String BROADCAST_KEY = "service_broadcast";

    public final static int WHAT_FUNC = 0;
        public final static String FUNC_NAME = "service_func_name";
            public static final String ON_CONNECT = "onConnect";
            public static final String ON_DISCONNECT = "onDisconnect";
            public static final String ON_CONNECTION_FAILED = "onConnectionFailed";

            public static final String REGISTER = "register";
            public static final String ON_REGISTER_RESPONSE = "onRegisterResponse";
            public static final String LOGIN = "login";
            public static final String ON_LOGIN_RESPONSE = "onLoginResponse";
            public static final String NAME_CHANGE = "changedName";
            public static final String ON_NAME_CHANGE_RESPONSE = "onNameChangeResponse";
            public static final String ON_STATUS_ERROR = "onStatusError";

        public final static String FUNC_ARGS = "service_func_args";
}
