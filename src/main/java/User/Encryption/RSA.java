package User.Encryption;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class RSA {

    private final static String ALGORITHM_TYPE = "RSA";

    public static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM_TYPE);
        return keyPairGenerator.generateKeyPair();
    }

    public static PublicKey getPublicKeyFromBytes(byte[] data) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return KeyFactory.getInstance(ALGORITHM_TYPE).generatePublic(new X509EncodedKeySpec(data));
    }

    public static PrivateKey getPrivateKeyFromBytes(byte[] data) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return KeyFactory.getInstance(ALGORITHM_TYPE).generatePrivate(new PKCS8EncodedKeySpec(data));
    }

//    public static byte[] encrypt(String data, PublicKey publicKey) {
//
//    }
}
