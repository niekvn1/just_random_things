package knickknacker.serialization;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/** A ByteArrayOutputStream that can be easily converted to an
 * input stream. */
public class ByteOutInStream extends ByteArrayOutputStream {
    public ByteOutInStream() {
        super();
    }

    public ByteOutInStream(int size) {
        super(size);
    }

    public int getCount() {
        return this.count;
    }

    public ByteArrayInputStream getInputStream() {
        ByteArrayInputStream in = new ByteArrayInputStream(this.buf, 0, this.count);
        this.buf = null;
        return in;
    }
}
