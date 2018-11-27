package knickknacker.niekserver.Networking;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

public class Session {
    HashMap<String, SessionUser> sessions;
    private static final int max = 1000000000;
    private static final int min = 1000000;

    public Session() {
        sessions = new HashMap<>();
    }

    public String add(String username) {
        String key = generateHash();
        sessions.put(key , new SessionUser(username));
        return key;
    }

    public void remove(String key) {
        if (sessions.containsKey(key)) {
            sessions.remove(key);
        }
    }

    public SessionUser getUser(String key) {
        if (key == null) {
            return null;
        } else {
            return sessions.get(key);
        }
    }

    private String generateHash() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    public class SessionUser {
        String username;

        private SessionUser(String username) {
            this.username = username;
        }

        public String getUsername() {
            return username;
        }
    }
}
