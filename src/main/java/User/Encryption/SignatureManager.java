package User.Encryption;

import java.security.*;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PSSParameterSpec;
import java.util.logging.Logger;

public class SignatureManager {
    private static final Logger LOGGER = Logger.getLogger(SignatureManager.class.getName());
    private final static String SIGNATURE_ALGORITHM = "RSASSA-PSS";
    private final static String SIGNATURE_HASH_NAME = "SHA-256";
    private final static String MASK_GENERATION_FUNCTION_NAME = "MGF1";
    private final static int SALT_LENGTH = 32;
    private final static int TRAILER_FIELD = 1;
    private static SignatureManager instance;
    private Signature signature;

    public SignatureManager() {
        signatureInit();
    }

    public static SignatureManager getInstance() {
        if (instance == null) {
            instance = new SignatureManager();
        }
        return instance;
    }

    private void signatureInit() {
        try {
            this.signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            this.signature.setParameter(new PSSParameterSpec(SIGNATURE_HASH_NAME, MASK_GENERATION_FUNCTION_NAME, MGF1ParameterSpec.SHA256, SALT_LENGTH, TRAILER_FIELD));
        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
    }

    public byte[] sign(byte[] data, PrivateKey privateKey) throws InvalidKeyException, SignatureException {
        signature.initSign(privateKey);
        signature.update(data);
        return signature.sign();
    }

    public boolean verify(byte[] data, byte[] signatureData, PublicKey publicKey) {
        try {
            signature.initVerify(publicKey);
            signature.update(data);
            return signature.verify(signatureData);
        } catch (InvalidKeyException | SignatureException e) {
            LOGGER.warning("Signature error. Reason " + e.toString());
        }
        return false;
    }

}
