package knickknacker.tcp.Signables;

import java.io.Serializable;

public class Signable implements Serializable {
    private String timestamp;

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
