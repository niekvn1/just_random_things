package knickknacker.sanderpunten.Services;

/**
 * Created by Niek on 31-12-2017.
 *
 * This class contains the constants that are used as types for the messages between activities and
 * the RoomService.
 */

public abstract class ServiceTypes {
    public final static String BROADCAST_KEY = "service_broadcast";

    /** Keys for default data types that can be send to the RoomService. */
    public final static String STRING_KEY = "service_string";
    public final static String INT_KEY = "service_int";
    public final static String LONG_KEY = "serive_long";
    public final static String BOOLEAN_KEY = "service_boolean";
    public final static String OBJECT_KEY = "service_object";
    public final static String OBJECT_TYPE = "service_object_name";

    /** The type of an object. */

    /** Broadcast reasons. */
    public final static String BROADCAST_TYPE = "broadcast_type";
        public final static byte FAILED_TO_CONNECT = 0;
        public final static byte CONNECTED = 1;
        public final static byte DISCONNECTED = 2;
        public final static byte REGISTER_RESPONSE = 3;

    public final static int WHAT_REGISTER = 0;
}
