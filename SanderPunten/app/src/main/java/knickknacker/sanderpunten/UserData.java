package knickknacker.sanderpunten;

import java.io.Serializable;

/**
 * Created by Niek on 2-6-2018.
 */

public class UserData implements Serializable {
    private long id;
    private String name;
    private long sanderpunten;

    public UserData(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
