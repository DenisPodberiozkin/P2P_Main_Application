package User.NodeManager.Lookup;

import User.NodeManager.Node;

public class FindLookupLogic implements LookupLogic {
    private final Node successor;
    private final String searchId;

    public FindLookupLogic(Node successor, String searchId) {
        this.successor = successor;
        this.searchId = searchId;
    }

    @Override
    public String finish() {
        return successor.getJSONString();
    }

    @Override
    public String proceed(Node node) {
        return node.findNode(searchId);
    }
}
