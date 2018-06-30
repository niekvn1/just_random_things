package knickknacker.sanderpunten.Storage;

import knickknacker.tcp.Signables.PublicUserData;

public class LocalStorage {
    private PublicUserData publicUserData = null;

    public LocalStorage() {

    }

    public PublicUserData getPublicUserData() {
        return publicUserData;
    }

    public void setPublicUserData(PublicUserData publicUserData) {
        this.publicUserData = publicUserData;
    }
}
