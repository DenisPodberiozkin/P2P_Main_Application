package User.NodeManager.MessageSession;

import Encryption.DH;
import User.NodeManager.User;

import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

public class InboundMessageSession extends MessageSession {
    private static final Logger LOGGER = Logger.getLogger(InboundMessageSession.class.getName());
    private final PublicKey receivedPublicKey;
    private final DH dh = new DH();
    private final CountDownLatch latch = new CountDownLatch(1);
    private PublicKey publicKeyToSend;

    public InboundMessageSession(long participantMessageSessionId, PublicKey receivedPublicKey) throws GeneralSecurityException {
        super(participantMessageSessionId);
        this.receivedPublicKey = receivedPublicKey;
        createSecureMessageSession();
    }

    @Override
    public String call() throws InterruptedException {
        latch.await();
        User user = User.getInstance();
        LOGGER.info("Received message encrypted message payload " + messageText);
        final String[] decryptedPayloadTokens = encryptionController.decryptStringByAES(secretKey, messageText).split(" ");
        final String senderId = decryptedPayloadTokens[0];
        messageText = decryptedPayloadTokens[1];
        user.addMessage(senderId, senderId, messageText);
        LOGGER.info("Received message " + messageText + " from " + senderId);
        return "OK";

    }

    private void createSecureMessageSession() throws GeneralSecurityException {
        this.publicKeyToSend = dh.initReceiver(receivedPublicKey);
        this.secretKey = dh.initSecretKey(receivedPublicKey);
    }

    public void messageNotify(String encryptedPayload) {
        messageText = encryptedPayload;
        latch.countDown();
    }

    public PublicKey getPublicKeyToSend() {
        return publicKeyToSend;
    }
}
