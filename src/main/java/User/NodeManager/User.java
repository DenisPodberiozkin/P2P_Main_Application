package User.NodeManager;

import java.security.PrivateKey;
import java.util.SortedMap;

public class User extends Node {
    private Node predecessor;
    private Node successor;
    private SortedMap<String, Node> nodes;
    private PrivateKey privateKey;


    public Node getPredecessor() {
        return predecessor;
    }

    public void setPredecessor(Node predecessor) {
        this.predecessor = predecessor;
    }

    public Node getSuccessor() {
        return successor;
    }

    public void setSuccessor(Node successor) {
        this.successor = successor;
    }

    public SortedMap<String, Node> getNodes() {
        return nodes;
    }

    public void setNodes(SortedMap<String, Node> nodes) {
        this.nodes = nodes;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(PrivateKey privateKey) {
        this.privateKey = privateKey;
    }
}
