package knickknacker.serialization;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by Niek on 28-10-17.
 *
 * Functions to Serialize and Deserialize data.
 */

public abstract class Serialize {
    private static ByteOutInStream createOut(Serializable o) throws IOException {
        ByteOutInStream outStream = new ByteOutInStream();
        ObjectOutputStream objectOutStream = new ObjectOutputStream(outStream);
        objectOutStream.writeObject(o);
        objectOutStream.flush();
        objectOutStream.close();
        outStream.close();
        return outStream;
    }


    /** Source:
     * http://www.java2s.com/Code/Android/Development/Functionthatgetthesizeofanobject.htm
     * Create a byte array of an object. */
    public static byte[] serialize(Serializable o) {
        try {
            ByteOutInStream outStream = createOut(o);
            return outStream.toByteArray();
        } catch(IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Object deserializeFromByteArray(byte[] b, int off, int len) {
        ByteOutInStream outStream = new ByteOutInStream();
        outStream.write(b, off, len);
        ByteArrayInputStream inStream = outStream.getInputStream();
        try {
            ObjectInputStream objectInStream = new ObjectInputStream(inStream);
            Object o = objectInStream.readObject();
            objectInStream.close();
            inStream.close();
            return o;
        } catch(ClassNotFoundException e) {
            e.printStackTrace();
        } catch(IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /** Create an object from a byte array. */
    public static Object deserialize(byte[] b) {
        ByteArrayInputStream inStream = new ByteArrayInputStream(b);
        try {
            ObjectInputStream objectInStream = new ObjectInputStream(inStream);
            Object o = objectInStream.readObject();
            objectInStream.close();
            inStream.close();
            return o;
        } catch(ClassNotFoundException e) {
            e.printStackTrace();
        } catch(IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static int serializeToByteArray(Serializable o, byte[] b, int off, int len) {
        try {
            ByteOutInStream outStream = createOut(o);
            int count = outStream.getCount();
            if (len < 0) {
                len = count;
            }

            ByteArrayInputStream in = outStream.getInputStream();
            int read = in.read(b, off, len);
            if (read < len) {
                return -1;
            }

            return read;
        } catch(IOException e) {
            e.printStackTrace();
        }

        return -1;
    }
}
