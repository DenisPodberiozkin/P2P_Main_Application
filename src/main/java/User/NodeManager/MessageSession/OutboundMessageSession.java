package User.NodeManager.MessageSession;

import User.Encryption.DH;
import User.NodeManager.User;

import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.util.Base64;
import java.util.logging.Logger;

public class OutboundMessageSession extends MessageSession {
    private static final Logger LOGGER = Logger.getLogger(OutboundMessageSession.class.getName());
    private final User user = User.getInstance();


    OutboundMessageSession(String participantNodeId, String messageText) {
        super(participantNodeId, messageText);
    }

    @Override
    public String call() throws GeneralSecurityException {
        createSecureMessageSession();
        return sendMessageViaSecureSession();

    }

    private void createSecureMessageSession() throws GeneralSecurityException {
        DH dh = new DH();
        final PublicKey publicKey = dh.initSender();
        final String publicKeyToSend64 = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        final String reply = user.transferPublicKey(participantNodeId, publicKeyToSend64, id);
        final String[] tokens = reply.split(" ");
        participantMessageSessionId = Long.parseLong(tokens[0]);
        final String receivedPublicKey64 = tokens[1];
        final byte[] receivedPublicKeyData = Base64.getDecoder().decode(receivedPublicKey64);
        PublicKey receivedPublicKey = DH.getDHPublicKeyFromData(receivedPublicKeyData);
        this.secretKey = dh.initSecretKey(receivedPublicKey);
        LOGGER.info("Secret key " + Base64.getEncoder().encodeToString(secretKey.getEncoded()) + " was created in session " + id);
    }

    private String sendMessageViaSecureSession() {
        final String senderId = user.getId();
        final String payload = senderId + " " + messageText;
        final String encryptedPayload = encryptionController.encryptStringByAES(secretKey, payload);
        return user.transferMessage(participantNodeId, participantMessageSessionId + " " + encryptedPayload);

    }
}
