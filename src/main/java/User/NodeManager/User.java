package User.NodeManager;

import User.ConnectionsData;
import User.Encryption.EncryptionController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.URL;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Collections;
import java.util.Comparator;
import java.util.SortedMap;
import java.util.TreeMap;

public class User extends Node {
    private Node predecessor;
    private Node successor;
    private final SortedMap<String, Node> nodes = Collections.synchronizedSortedMap(new TreeMap<String, Node>(Comparator.naturalOrder()));
    private PrivateKey privateKey;
    private static User instance;

    public User(KeyPair keyPair) {
        this(keyPair.getPublic());
        this.privateKey = keyPair.getPrivate();
    }

    public User(PublicKey publicKey) {
        super(publicKey, ConnectionsData.getUserServerPort());
        initialiseIP();
        User.instance = this;
    }

    public User() {
        super(EncryptionController.getInstance().generateKeyPair().getPublic(), ConnectionsData.getUserServerPort());
        initialiseIP();
        User.instance = this;
    }

    public static User getInstance() {
        return instance;
    }

    private void initialiseIP() {
        try (final DatagramSocket socket = new DatagramSocket()) {
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            String ip = socket.getLocalAddress().getHostAddress();
            String publicIp = initialisePublicIp();
            setIp(ip);
            setPublicIp(publicIp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String initialisePublicIp() throws IOException {
        URL whatIsMyIp = new URL("http://checkip.amazonaws.com");
        try (final BufferedReader in = new BufferedReader(new InputStreamReader(whatIsMyIp.openStream()))) {
            return in.readLine();
        }
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(PrivateKey privateKey) {
        this.privateKey = privateKey;
    }

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

    public Node getNodeFromTable(String id) {
        return nodes.get(id);
    }

    public boolean hasNeighbours() {
        return !nodes.isEmpty();
    }
}
