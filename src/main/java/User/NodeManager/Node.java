package User.NodeManager;

import User.Encryption.EncryptionController;
import User.Encryption.EncryptionUtil;

import java.security.PublicKey;
import java.util.Objects;

public class Node {
    private final String id;
    private String ip;
    private String publicIp;
    private int port;
    private final PublicKey publicKey;

    public Node(PublicKey publicKey, int port) {
        this.publicKey = publicKey;
        this.id = generateId();
        this.port = port;
    }

    private String generateId() {
        return EncryptionUtil.byteToHex(EncryptionController.getInstance().hash(publicKey.getEncoded()));
    }

    public String getId() {
        return id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPublicIp() {
        return publicIp;
    }

    public void setPublicIp(String publicIp) {
        this.publicIp = publicIp;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Object lookup(String id) {
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Node)) return false;
        Node node = (Node) o;
        return id.equals(node.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
