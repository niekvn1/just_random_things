package knickknacker.sanderserver.TCP;

/**
 * Created by Niek on 3-11-17.
 *
 * Message types used to determine the target and kind of a message after receiving it from the
 * network.
 */

public abstract class MessageTypes {
    public final static byte PING = 0;
    public final static byte PONG = 1;
    public final static byte REGISTER = 2;
    public final static byte REGISTER_RESPONSE = 3;
    public final static byte LOGIN = 4;

    /** Data types. */
    public final static String INT = "msg_int";
    public final static String LONG = "msg_long";
    public final static String STRING = "msg_string";

}
