package knickknacker.tcp.Signables;

import java.io.Serializable;

public class SignableObject extends Signable {
    private Serializable object;

    public SignableObject(int id, Serializable object) {
        super(id);
        this.object = object;
    }

    public Serializable getObject() {
        return object;
    }

    public void setObject(Serializable object) {
        this.object = object;
    }
}
