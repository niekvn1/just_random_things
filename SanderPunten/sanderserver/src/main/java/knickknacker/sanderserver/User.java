package knickknacker.sanderserver;

import java.security.PublicKey;

import knickknacker.shared.Signables.PublicUserData;

public class User {
    private PublicUserData publicUserData;
    private PublicKey publicKey;
    private boolean verifiedLogin = false;
    private String address = null;
    private int port = -1;

    public User(PublicUserData publicUserData, PublicKey publicKey) {
        this.publicUserData = publicUserData;
        this.publicKey = publicKey;
    }

    public PublicUserData getPublicUserData() {
        return publicUserData;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicUserData(PublicUserData publicUserData) {
        this.publicUserData = publicUserData;
    }

    public boolean isVerifiedLogin() {
        return verifiedLogin;
    }

    public void setVerifiedLogin(boolean verifiedLogin) {
        this.verifiedLogin = verifiedLogin;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
