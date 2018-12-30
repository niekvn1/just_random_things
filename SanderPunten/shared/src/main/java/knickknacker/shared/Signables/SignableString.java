package knickknacker.shared.Signables;

import java.io.Serializable;

public class SignableString extends Signable implements Serializable {
    private String string;

    public SignableString(int userId, String string) {
        super(userId);
        this.string = string;
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }
}
