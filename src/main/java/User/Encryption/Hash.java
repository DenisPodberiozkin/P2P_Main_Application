package User.Encryption;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hash {
    private static Hash instance;

    public static Hash getInstance() {
        if (instance == null) {
            instance = new Hash();
        }
        return instance;
    }

    public byte[] hash(byte[] bytes) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            return digest.digest(bytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}
