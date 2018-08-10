package knickknacker.tcp;

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
    /** Source:
     * http://www.java2s.com/Code/Android/Development/Functionthatgetthesizeofanobject.htm
     * Create a byte array of an object. */
    public static byte[] serialize(Serializable o) {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        try {
            ObjectOutputStream objectOutStream = new ObjectOutputStream(outStream);
            objectOutStream.writeObject(o);
            objectOutStream.flush();
            objectOutStream.close();
            outStream.close();

            byte[] bytes = outStream.toByteArray();
            return bytes;
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
}
