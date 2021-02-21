package User.NodeManager;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.SortedMap;

public class User extends Node {
    private Node predecessor;
    private Node successor;
    private SortedMap<String, Node> nodes;
    private PrivateKey privateKey;

    public User(KeyPair keyPair) {
        super(keyPair.getPublic());
        this.privateKey = keyPair.getPrivate();
    }

    public User(PublicKey publicKey) {
        super(publicKey);
    }

    public void setPrivateKey(PrivateKey privateKey) {
        this.privateKey = privateKey;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }
}
