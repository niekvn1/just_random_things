package knickknacker.tcp;

import java.io.Serializable;

/**
 * Created by Niek on 28-10-17.
 *
 * Other kind of messages are wrapped within this message object before they are sent over the
 * network. On the other side the will be unwrapped and used.
 */

public class RemoteCall implements Serializable {
    private String func;
    private Object data;
    private byte[] signature;

    public RemoteCall(String func, Object data, byte[] signature) {
        this.func = func;
        this.data = data;
        this.signature = signature;
    }

    public String getFunc() {
        return func;
    }

    public Object getData() {
        return data;
    }

    public byte[] getSignature() {
        return signature;
    }

    public byte[] encode() {
        return Serialize.serialize(this);
    }

    public static RemoteCall decode(byte[] bytes) {
        Object object = Serialize.deserialize(bytes);
        if (object instanceof RemoteCall) {
            return ((RemoteCall) object);
        } else {
            return null;
        }
    }
}

