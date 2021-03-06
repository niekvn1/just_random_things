package knickknacker.tcp.Services;

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

    /** Broadcast reasons. */
    public final static String BROADCAST_TYPE = "broadcast_type";
}
