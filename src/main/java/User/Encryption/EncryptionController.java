package User.Encryption;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;

public class EncryptionController implements IEncryptionController {

    private static EncryptionController instance;

    public static EncryptionController getInstance() {
        if (instance == null) {
            instance = new EncryptionController();
        }
        return instance;
    }

    public KeyPair generateKeyPair() {
        try {
            return RSA.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Error while generating RSA keypair");
            e.printStackTrace();
        }
        return null;
    }
}
