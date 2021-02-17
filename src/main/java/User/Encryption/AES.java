package User.Encryption;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

public class AES {


    private final static int SECRET_KEY_SIZE = 32; // 32 bytes = 256 bits
    private final static String SECRET_KEY_TYPE = "PBKDF2WithHmacSHA512"; // 512 bit hash
    private final static int PBE_ITERATIONS_COUNT = 65536;
    private final static int KDF_HASH_SIZE = 256;
    private final static String KEY_TYPE = "AES";
    private final static String CIPHER_TYPE = "AES/GCM/NoPadding";
    private final static int INITIALIZATION_VECTOR_SIZE = 12; // 12 bytes = 96 bit
    private final static int AUTHENTICATION_TAG_LENGTH = 16 * 8; // 128 bits

    public String generateSecretPassword() throws NoSuchAlgorithmException {
        SecureRandom secureRandom = SecureRandom.getInstanceStrong();
        byte[] secretKeyBytes = new byte[SECRET_KEY_SIZE];
        secureRandom.nextBytes(secretKeyBytes);
        return Base64.getEncoder().encodeToString(secretKeyBytes);
    }


    public SecretKey generateSecretKey(String password, String secretPassword) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(SECRET_KEY_TYPE);

        KeySpec spec = new PBEKeySpec(password.toCharArray(), secretPassword.getBytes(), PBE_ITERATIONS_COUNT, KDF_HASH_SIZE);

        SecretKey secretKDFKey = secretKeyFactory.generateSecret(spec);

        return new SecretKeySpec(secretKDFKey.getEncoded(), KEY_TYPE);

    }

    public byte[] encryptFile(SecretKey key, File file) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {

        Cipher cipher = Cipher.getInstance(CIPHER_TYPE);
        byte[] initializationVector = generateInitializationVector();

        GCMParameterSpec spec = new GCMParameterSpec(AUTHENTICATION_TAG_LENGTH, initializationVector);

        cipher.init(Cipher.ENCRYPT_MODE, key, spec);

        byte[] fileData = readFile(file);
        if (fileData != null) {
            byte[] encryptedData = cipher.doFinal(fileData);
            ByteBuffer byteBuffer = ByteBuffer.allocate(INITIALIZATION_VECTOR_SIZE + encryptedData.length);
            byteBuffer.put(initializationVector);
            byteBuffer.put(encryptedData);
            return byteBuffer.array();
        }

        return null;

    }


    public byte[] decryptFile(SecretKey secretKey, File file) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {

        byte[] fileData = readFile(file);
        if (fileData != null) {

            ByteBuffer byteBuffer = ByteBuffer.wrap(fileData);
            byte[] initializationVector = new byte[INITIALIZATION_VECTOR_SIZE];
            byteBuffer.get(initializationVector);

            byte[] encryptedData = new byte[byteBuffer.remaining()];
            byteBuffer.get(encryptedData);

            Cipher cipher = Cipher.getInstance(CIPHER_TYPE);
            GCMParameterSpec spec = new GCMParameterSpec(AUTHENTICATION_TAG_LENGTH, initializationVector);

            cipher.init(Cipher.DECRYPT_MODE, secretKey, spec);
            return cipher.doFinal(encryptedData);

        }

        return null;
    }

    private byte[] readFile(File file) {
        try (FileInputStream inputStream = new FileInputStream(file)) {
            byte[] fileData = new byte[(int) file.length()];
            int numberOfBytesRead = inputStream.read(fileData);
            if (file.length() == numberOfBytesRead) {
                return fileData;
            }
        } catch (IOException e) {
            System.err.println("Error while reading file to decrypt");
            e.printStackTrace();

        }

        return null;
    }

    private byte[] generateInitializationVector() throws NoSuchAlgorithmException {
        SecureRandom secureRandom = SecureRandom.getInstanceStrong();
        byte[] ivBytes = new byte[INITIALIZATION_VECTOR_SIZE];
        secureRandom.nextBytes(ivBytes);
        return ivBytes;
    }


}
