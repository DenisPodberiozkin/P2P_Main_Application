package User.NodeManager.Lookup;

import User.NodeManager.Node;
import User.NodeManager.User;

public class TransferMessageLookupLogic implements LookupLogic {
    private final String receiverId;
    private final String payload;

    public TransferMessageLookupLogic(String receiverId, String payload) {
        this.receiverId = receiverId;
        this.payload = payload;
    }

    @Override
    public String finish() {
        final User user = User.getInstance();
        if (user.getId().equals(receiverId)) {
            user.processMessage(payload);
            return "OK";
        }
        return "NF";
    }

    @Override
    public String proceed(Node node) {
        return node.transferMessage(receiverId, payload);
    }
}
