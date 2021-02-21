package User.Encryption;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.io.File;
import java.security.*;
import java.security.spec.InvalidKeySpecException;

public class EncryptionController implements IEncryptionController {

    private static EncryptionController instance;

    public static EncryptionController getInstance() {
        if (instance == null) {
            instance = new EncryptionController();
        }
        return instance;
    }

    @Override
    public KeyPair generateKeyPair() {
        try {
            return RSA.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Error while generating RSA keypair");
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public byte[] hash(byte[] bytes) {
        return Hash.getInstance().hash(bytes);
    }

    @Override
    public String generateSecretPassword() {
        try {
            return AES.generateSecretPassword();
        } catch (Exception e) {
            System.err.println("Error generating secret password");
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public SecretKey generateAESKey(String password, String secretPassword) {
        try {
            return AES.generateSecretKey(password, secretPassword);
        } catch (Exception e) {
            System.err.println("Error generating secret AES key");
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public byte[] encryptFileByAES(SecretKey key, File file) {
        try {
            return AES.encryptFile(key, file);
        } catch (Exception e) {
            System.err.println("Error encrypting file: " + file.getPath());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public byte[] encryptFileByAES(SecretKey key, byte[] fileData) {
        try {
            return AES.encryptFile(key, fileData);
        } catch (Exception e) {
            System.err.println("Error encrypting file data");
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public byte[] decryptFileByAES(SecretKey secretKey, File file) {
        try {
            return AES.decryptFile(secretKey, file);
        } catch (NoSuchAlgorithmException | InvalidKeyException | InvalidAlgorithmParameterException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException e) {
            System.err.println("Error decrypting file: " + file.getPath());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public byte[] decryptFileByAES(SecretKey secretKey, byte[] fileData) {
        try {
            return AES.decryptFile(secretKey, fileData);
        } catch (NoSuchAlgorithmException | InvalidKeyException | InvalidAlgorithmParameterException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException e) {
            System.err.println("Error decrypting file data");
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public PublicKey getPublicKeyFromBytes(byte[] data) {
        try {
            return RSA.getPublicKeyFromBytes(data);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            System.err.println("Error while generating Public Key form data");
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public PrivateKey getPrivateKeyFromBytes(byte[] data) {
        try {
            return RSA.getPrivateKeyFromBytes(data);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            System.err.println("Error while generating Private Key form data");

            e.printStackTrace();
        }
        return null;
    }

}
