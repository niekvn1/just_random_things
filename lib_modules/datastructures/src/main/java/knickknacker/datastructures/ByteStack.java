package knickknacker.datastructures;

import android.util.Log;

import java.io.Serializable;
import java.lang.reflect.Member;
import java.util.Stack;

import knickknacker.serialization.Serialize;

public class ByteStack implements Serializable {
    private byte[] stack;
    private int size;
    private int cursor = 0;
    private Stack<Integer> lengths;

    public ByteStack(int size) {
        this.size = size;
        stack = new byte[this.size];
        lengths = new Stack<>();
    }

    public boolean push(Serializable object) {
        int read = Serialize.serializeToByteArray(object, stack, cursor, -1);
        Class<?> cls = object.getClass();
        Log.i("ByteStack", "Class=" + cls.toString());
        if (read < 0) {
            return false;
        }

        cursor += read;
        lengths.push(read);
        return true;
    }

    public <T> T pop(Class<T> cls) {
        int len = lengths.pop();
        cursor -= len;
        Object o = Serialize.deserializeFromByteArray(stack, cursor, len);
        if (o == null) {
            return null;
        }

        return cast(o, cls);
    }

    public int getSize() {
        return size;
    }

    public int getCursor() {
        return cursor;
    }

    private <T> T cast(Object o, Class<T> clazz) {
        return clazz.isInstance(o) ? clazz.cast(o) : null;
    }
}
