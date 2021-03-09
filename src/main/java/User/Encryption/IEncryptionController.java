package User.Encryption;

import javax.crypto.SecretKey;
import java.io.File;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

public interface IEncryptionController {
    KeyPair generateRSAKeyPair();

    byte[] hash(byte[] bytes);

    String generateSecretPassword();

    SecretKey generateAESKey(String password, String secretPassword);

    byte[] encryptFileByAES(SecretKey key, File file);

    byte[] encryptDataByAES(SecretKey key, byte[] data);

    byte[] decryptFileByAES(SecretKey secretKey, File file);

    String encryptStringByAES(SecretKey key, String s);

    String decryptStringByAES(SecretKey key, String s);

    byte[] decryptDataByAES(SecretKey secretKey, byte[] data);

    PublicKey getPublicKeyFromBytes(byte[] data);

    PrivateKey getPrivateKeyFromBytes(byte[] data);
}
