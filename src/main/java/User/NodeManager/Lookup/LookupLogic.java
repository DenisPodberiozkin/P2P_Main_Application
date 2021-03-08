package User.NodeManager.Lookup;

import User.NodeManager.Node;

public interface LookupLogic {
    String finish();

    String proceed(Node node);
}
