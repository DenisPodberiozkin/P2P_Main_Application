package User.Encryption;

import Encryption.AES;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.io.File;
import java.io.FileOutputStream;
import java.security.NoSuchAlgorithmException;

class AESTest {

    @Test
    void generateSecretKey() {
        try {
            System.out.println(AES.generateSecretPassword());
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
            String password = "123456789";
            String secretPassword = AES.generateSecretPassword();
            SecretKey key = AES.generateSecretKey(password, secretPassword);
            File file = new File("src/main/resources/db/Userdata.db");

            byte[] encryptedData = AES.encryptFile(key, file);
            FileOutputStream outputStream = new FileOutputStream("src/main/resources/db/EncryptedUserdata.db");
            outputStream.write(encryptedData);
            outputStream.close();

            File encryptedFile = new File("src/main/resources/db/EncryptedUserdata.db");

            key = AES.generateSecretKey(password, secretPassword);

            byte[] decryptedData = AES.decryptFile(key, encryptedFile);

            outputStream = new FileOutputStream("src/main/resources/db/DecryptedUserdata.db");
            outputStream.write(decryptedData);
            outputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}