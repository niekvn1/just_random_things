package knickknacker.tcp.Protocol;

import knickknacker.tcp.RemoteCall;
import knickknacker.tcp.Serialize;

public abstract class SanderServerProtocol {
    public static final String STATUS_WTF = "SANDER/1.0 0 WTF_ARE_YOU_DOING";
    public static final String STATUS_OK = "SANDER/1.0 1 OK";
    public static final String STATUS_BAD_REQUEST = "SANDER/1.0 2 BAD_REQUEST";
    public static final String STATUS_VERIFICATION_FAILURE = "SANDER/1.0 3 VERIFICATION_FAILURE";
    public static final String STATUS_NOT_FOUND = "SANDER/1.0 4 NOT_FOUND";
    public static final String STATUS_INVALID_ARGS = "SANDER/1.0 5 INVALID_ARGS";
    public static final String STATUS_INVALID_TIME = "SANDER/1.0 6 INVALID_TIME";
    public static final String STATUS_JAVA_EXCEPTION = "SANDER/1.0 7 JAVA_EXCEPTION";

    public static final String KEY_EXCEPTION = "EXCEPTION";

    public static final String FUNC_SERVER_EXCEPTION = "onServerException";
    public static final String FUNC_REGISTER = "onRegister";
    public static final String FUNC_REGISTER_RESPONSE = "onRegisterResponse";
    public static final String FUNC_LOGIN = "onLogin";
    public static final String FUNC_LOGIN_RESPONSE = "onLoginResponse";
    public static final String FUNC_NAME_CHANGE = "onNameChange";
    public static final String FUNC_NAME_CHANGE_RESPONSE = "onNameChangeResponse";
    public static final String FUNC_CHAT_SEND = "onChatSend";
    public static final String FUNC_CHAT_RECEIVE = "onChatReceive";
    public static final String FUNC_GET_USERS = "onGetUsers";
    public static final String FUNC_GET_USERS_RESPONSE = "onGetUsersResponse";
    public static final String FUNC_ADDED_SANDERPUNTEN = "onAddedSanderPunten";
    public static final String FUNC_ADDED_SANDERPUNTEN_BROADCAST = "onAddedSanderPuntenBroadcast";
    public static final String FUNC_ADMIN_APPLY = "onAdminApply";
    public static final String FUNC_ADMIN_APPLY_RESPONSE = "onAdminApplyResponse";

    public static byte[] stringResponse(String func, String data) {
        RemoteCall call = new RemoteCall(func, data, null);
        byte[] bytes = Serialize.serialize(call);
        return bytes;
    }

    public static byte[] wtf(String func) {
        return stringResponse(func, STATUS_WTF);
    }

    public static byte[] ok(String func) {
        return stringResponse(func, STATUS_OK);
    }

    public static byte[] badRequest(String func) {
        return stringResponse(func, STATUS_BAD_REQUEST);
    }

    public static byte[] verificationFailure(String func) {
        return stringResponse(func, STATUS_VERIFICATION_FAILURE);
    }

    public static byte[] notFound(String func) {
        return stringResponse(func, STATUS_NOT_FOUND);
    }

    public static byte[] invalidArgs(String func) {
        return stringResponse(func, STATUS_INVALID_ARGS);
    }

    public static byte[] invalidTime(String func) {
        return stringResponse(func, STATUS_INVALID_TIME);
    }

    public static byte[] javaException(String func, String type) {
        return stringResponse(func,
                    STATUS_JAVA_EXCEPTION + "\n"
                    + KEY_EXCEPTION + ':' + type
                );
    }
}
