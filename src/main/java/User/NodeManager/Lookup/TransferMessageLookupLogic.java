package User.NodeManager.Lookup;

import User.NodeManager.Exceptions.InboundMessageSessionNotFound;
import User.NodeManager.Node;
import User.NodeManager.User;

import java.util.logging.Logger;

public class TransferMessageLookupLogic implements LookupLogic {
    private static final Logger LOGGER = Logger.getLogger(TransferMessageLookupLogic.class.getName());
    private final String receiverId;
    private final String payload;
    private final User user;
    private final String userId;

    public TransferMessageLookupLogic(String receiverId, String payload) {
        this.receiverId = receiverId;
        this.payload = payload;
        this.user = User.getInstance();
        this.userId = this.user.getId();
    }

    @Override
    public String finish() {
        try {
            if (userId.equals(receiverId)) {
                user.processMessage(payload);
                return "OK";
            }
        } catch (InboundMessageSessionNotFound inboundMessageSessionNotFound) {
            LOGGER.warning(inboundMessageSessionNotFound.toString());
            return "ERROR " + inboundMessageSessionNotFound.toString();
        }
        return "NF";
    }

    @Override
    public String proceed(Node node) {
        if (userId.equals(receiverId)) {
            return finish();
        }
        return node.transferMessage(receiverId, payload);
    }
}
