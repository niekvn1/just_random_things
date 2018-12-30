package knickknacker.shared.Signables;

import java.io.Serializable;

public class Signable implements Serializable {
    private String timestamp;
    private int id;

    public Signable(int id) {
        this.id = id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
