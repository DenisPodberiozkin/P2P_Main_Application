package User.Encryption;

import Encryption.EncryptionController;
import org.junit.jupiter.api.Test;

import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PSSParameterSpec;
import java.util.Base64;

class RSATest {
    @Test
    public void signatureTest() {
        try {
            KeyPair keyPair = EncryptionController.getInstance().generateRSAKeyPair();
            RSAPublicKey publicKey = ((RSAPublicKey) keyPair.getPublic());
            RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
            System.out.println(privateKey.getModulus().toString());

            final String data = "TestData";
            final byte[] dataBytes = data.getBytes();

            Signature signature = Signature.getInstance("RSASSA-PSS");
            signature.setParameter(new PSSParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA256, 32, 1));
            signature.initSign(privateKey);
            signature.update(dataBytes);
            byte[] signatureData = signature.sign();

            System.out.println(signatureData.length);
            System.out.println(Base64.getEncoder().encodeToString(signatureData));


            signature.initVerify(publicKey);
            signature.update(dataBytes);
            System.out.println(signature.verify(signatureData));


        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException | InvalidKeyException | SignatureException e) {
            e.printStackTrace();
        }
    }

}