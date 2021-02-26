package User.Encryption;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hash {
    private static Hash instance;
    private final static String DIGEST_TYPE = "SHA-1";
    private static int hashSize; // MUST BE 160 bits


    public static byte[] hash(byte[] bytes) {
        try {
            MessageDigest digest = MessageDigest.getInstance(DIGEST_TYPE);
            return digest.digest(bytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int getHashSize() {
        if (hashSize == 0) {
            try {
                hashSize = MessageDigest.getInstance(DIGEST_TYPE).getDigestLength() * 8;
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
        return hashSize;
    }
}
