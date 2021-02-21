package User.Encryption;

import javax.crypto.SecretKey;
import java.io.File;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

public interface IEncryptionController {
    KeyPair generateKeyPair();

    byte[] hash(byte[] bytes);

    String generateSecretPassword();

    SecretKey generateAESKey(String password, String secretPassword);

    byte[] encryptFileByAES(SecretKey key, File file);

    byte[] encryptFileByAES(SecretKey key, byte[] fileData);

    byte[] decryptFileByAES(SecretKey secretKey, File file);

    byte[] decryptFileByAES(SecretKey secretKey, byte[] fileData);

    PublicKey getPublicKeyFromBytes(byte[] data);

    PrivateKey getPrivateKeyFromBytes(byte[] data);
}
