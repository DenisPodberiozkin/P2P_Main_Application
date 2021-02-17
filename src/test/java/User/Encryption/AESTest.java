package User.Encryption;

import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.io.File;
import java.io.FileOutputStream;
import java.security.NoSuchAlgorithmException;

class AESTest {

    @Test
    void generateSecretKey() {
        AES aes = new AES();
        try {
            System.out.println(aes.generateSecretPassword());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    @Test
    void maxAES() throws NoSuchAlgorithmException, ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
//        Security.setProperty("crypto.policy", "unlimited");


        int maxKeySize = javax.crypto.Cipher.getMaxAllowedKeyLength("AES");
        System.out.println("Max Key Size for AES : " + maxKeySize);
    }


    @Test
    void encryptionDecryptionTest() {
        try {
            AES aes = new AES();
            String password = "123456789";
            String secretPassword = aes.generateSecretPassword();
            SecretKey key = aes.generateSecretKey(password, secretPassword);
            File file = new File("src/main/resources/db/Userdata.db");

            byte[] encryptedData = aes.encryptFile(key, file);
            FileOutputStream outputStream = new FileOutputStream("src/main/resources/db/EncryptedUserdata.db");
            outputStream.write(encryptedData);
            outputStream.close();

            File encryptedFile = new File("src/main/resources/db/EncryptedUserdata.db");

            key = aes.generateSecretKey(password, secretPassword);

            byte[] decryptedData = aes.decryptFile(key, encryptedFile);

            outputStream = new FileOutputStream("src/main/resources/db/DecryptedUserdata.db");
            outputStream.write(decryptedData);
            outputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}