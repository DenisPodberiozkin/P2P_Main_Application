package User.NodeManager.Lookup;

import User.Encryption.DH;
import User.NodeManager.MessageSession.InboundMessageSession;
import User.NodeManager.Node;
import User.NodeManager.User;

import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.util.Base64;
import java.util.logging.Logger;

public class TransferPublicKeyLookupLogic implements LookupLogic {
    private static final Logger LOGGER = Logger.getLogger(TransferPublicKeyLookupLogic.class.getName());
    private final String receiverId;
    private final String publicKey64;
    private final long messageSessionId;
    private final User user;
    private final String userId;


    public TransferPublicKeyLookupLogic(String receiverId, String publicKey64, long messageSessionId) {
        this.receiverId = receiverId;
        this.publicKey64 = publicKey64;
        this.messageSessionId = messageSessionId;
        this.user = User.getInstance();
        this.userId = this.user.getId();
    }

    @Override
    public String finish() {
        String reply;
        if (receiverId.equals(userId)) {
            try {
                byte[] publicKeyData = Base64.getDecoder().decode(publicKey64);
                PublicKey publicKey = DH.getDHPublicKeyFromData(publicKeyData);
                final InboundMessageSession inboundMessageSession = user.createInboundMessageSession(messageSessionId, publicKey);
                final long inboundMessageSessionId = inboundMessageSession.getId();
                byte[] publicKeyToSendData = inboundMessageSession.getPublicKeyToSend().getEncoded();
                final String publicKeyToSend64 = Base64.getEncoder().encodeToString(publicKeyToSendData);
                reply = inboundMessageSessionId + " " + publicKeyToSend64;
            } catch (GeneralSecurityException e) {
                LOGGER.warning("Unable to create Inbound Message Session due to " + e.toString());
                reply = "Error. Unable to create Inbound Message Session due to " + e.toString();
            }
        } else {
            reply = "NF";
        }

        return reply;

    }

    @Override
    public String proceed(Node node) {
        if (receiverId.equals(userId)) {
            return finish();
        }
        return node.transferPublicKey(receiverId, publicKey64, messageSessionId);
    }
}
