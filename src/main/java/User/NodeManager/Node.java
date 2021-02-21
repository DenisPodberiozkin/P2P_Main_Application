package User.NodeManager;

import User.Encryption.EncryptionController;
import User.Encryption.EncryptionUtil;

import java.security.PublicKey;

public class Node {
    private final String id;
    private String ip;
    private String publicIp;
    private final PublicKey publicKey;

    public Node(PublicKey publicKey) {
        this.publicKey = publicKey;
        this.id = generateId();
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

}
