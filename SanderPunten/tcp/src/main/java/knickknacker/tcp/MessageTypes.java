package knickknacker.tcp;

/**
 * Created by Niek on 3-11-17.
 *
 * RemoteCall types used to determine the target and kind of a message after receiving it from the
 * network.
 */

public abstract class MessageTypes {
    public final static byte MSG_PING = 0;
    public final static byte MSG_PONG = 1;
    public final static byte MSG_REGISTER = 2;
    public final static byte MSG_REGISTER_RESPONSE = 3;
    public final static byte MSG_LOGIN = 4;

    /** Data types. */
    public final static String MSG_INT = "msg_int";
    public final static String MSG_LONG = "msg_long";
    public final static String MSG_STRING = "msg_string";

}
