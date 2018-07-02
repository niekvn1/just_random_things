package knickknacker.tcp.Signables;

import java.io.Serializable;

/**
 * Created by Niek on 2-6-2018.
 */

public class PublicUserData extends Signable implements Serializable {
    private String name;
    private long sanderpunten;

    public PublicUserData(int id) {
        super(id);
        sanderpunten = 0;
        name = "";
    }

    public PublicUserData(PublicUserData data) {
        super(data.getId());
        this.name = data.getName();
        this.sanderpunten = data.getSanderpunten();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSanderpunten() {
        return sanderpunten;
    }

    public void setSanderpunten(long sanderpunten) {
        this.sanderpunten = sanderpunten;
    }
}
