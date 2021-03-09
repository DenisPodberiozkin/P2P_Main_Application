package User.Encryption;

import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.logging.Logger;

public class DH {
    private static final Logger LOGGER = Logger.getLogger(DH.class.getName());
    private final static String ALGORITHM_TYPE = "DH";
    private final static String SECRET_KEY_TYPE = "AES";
    private KeyPair keyPair;
    private KeyAgreement keyAgreement;

    public static PublicKey getDHPublicKeyFromData(byte[] publicKeyData) throws GeneralSecurityException {
        return KeyFactory.getInstance(ALGORITHM_TYPE).generatePublic(new X509EncodedKeySpec(publicKeyData));
    }

    public PublicKey initSender() {
        try {
            keyPair = generateKeyPair();
            init();
            return keyPair.getPublic();
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            LOGGER.severe("Unable to init sender in DH. Reason " + e.toString());
        }
        return null;
    }

    public PublicKey initReceiver(PublicKey receivedPublicKey) throws InvalidAlgorithmParameterException {
        try {
            DHParameterSpec spec = ((DHPublicKey) receivedPublicKey).getParams();
            keyPair = generateKeyPairWithSpec(spec);
            init();
            return keyPair.getPublic();
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            LOGGER.severe("Unable to init receiver in DH. Reason " + e.toString());

        }
        return null;
    }

    private void init() throws NoSuchAlgorithmException, InvalidKeyException {
        keyAgreement = KeyAgreement.getInstance(ALGORITHM_TYPE);
        keyAgreement.init(keyPair.getPrivate());
    }

    public SecretKey initSecretKey(PublicKey receivedPublicKey) throws InvalidKeyException {
        keyAgreement.doPhase(receivedPublicKey, true);
        byte[] sharedSecret = keyAgreement.generateSecret();
        return new SecretKeySpec(sharedSecret, 0, 16, SECRET_KEY_TYPE);
    }

    private KeyPair generateKeyPairWithSpec(DHParameterSpec spec) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance(ALGORITHM_TYPE);
        generator.initialize(spec);
        return generator.generateKeyPair();
    }

    private KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance(ALGORITHM_TYPE);
        generator.initialize(2048);
        return generator.generateKeyPair();
    }


}
