package User.Encryption;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.io.File;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.logging.Logger;

public class EncryptionController implements IEncryptionController {
    private static final Logger LOGGER = Logger.getLogger(EncryptionController.class.getName());
    private static EncryptionController instance;

    public static EncryptionController getInstance() {
        if (instance == null) {
            instance = new EncryptionController();
        }
        return instance;
    }

    @Override
    public KeyPair generateRSAKeyPair() {
        try {
            return RSA.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            LOGGER.warning("Error while generating RSA keypair");
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public byte[] hash(byte[] bytes) {
        return Hash.hash(bytes);
    }

    @Override
    public String generateSecretPassword() {
        try {
            return AES.generateSecretPassword();
        } catch (Exception e) {
            LOGGER.warning("Error generating secret password");
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public SecretKey generateAESKey(String password, String secretPassword) {
        try {
            return AES.generateSecretKey(password, secretPassword);
        } catch (Exception e) {
            LOGGER.warning("Error generating secret AES key");
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public byte[] encryptFileByAES(SecretKey key, File file) {
        try {
            return AES.encryptFile(key, file);
        } catch (Exception e) {
            LOGGER.warning("Error encrypting file: " + file.getPath());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public byte[] encryptDataByAES(SecretKey key, byte[] data) {
        try {
            return AES.encryptData(key, data);
        } catch (Exception e) {
            LOGGER.warning("Error encrypting file data");
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public byte[] decryptFileByAES(SecretKey secretKey, File file) {
        try {
            return AES.decryptFile(secretKey, file);
        } catch (NoSuchAlgorithmException | InvalidKeyException | InvalidAlgorithmParameterException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException e) {
            LOGGER.warning("Error decrypting file: " + file.getPath());
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public String encryptStringByAES(SecretKey key, String s) {
        try {
            return AES.encryptString(key, s);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String decryptStringByAES(SecretKey key, String s) {
        try {
            return AES.decryptString(key, s);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public byte[] decryptDataByAES(SecretKey secretKey, byte[] data) {
        try {
            return AES.decryptData(secretKey, data);
        } catch (NoSuchAlgorithmException | InvalidKeyException | InvalidAlgorithmParameterException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException e) {
            LOGGER.warning("Error decrypting file data");
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public PublicKey getPublicKeyFromBytes(byte[] data) {
        try {
            return RSA.getPublicKeyFromBytes(data);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            LOGGER.warning("Error while generating Public Key form data");
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public PrivateKey getPrivateKeyFromBytes(byte[] data) {
        try {
            return RSA.getPrivateKeyFromBytes(data);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            LOGGER.warning("Error while generating Private Key form data");

            e.printStackTrace();
        }
        return null;
    }

}
