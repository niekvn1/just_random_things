package knickknacker.tcp;

import java.io.Serializable;

/**
 * Created by Niek on 28-10-17.
 *
 * Other kind of messages are wrapped within this message object before they are sent over the
 * network. On the other side the will be unwrapped and used.
 */

public class Message implements Serializable {
    private byte type;
    private Object data;

    public Message(byte type, Object data) {
        this.type = type;
        this.data = data;
    }

    public byte getType() {
        return this.type;
    }

    public Object getData() {
        return this.data;
    }
}

