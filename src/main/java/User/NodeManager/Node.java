package User.NodeManager;

import User.Encryption.EncryptionController;
import User.Encryption.EncryptionUtil;
import User.JSON.NodeDeserializer;
import User.JSON.NodeSerializer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.security.PublicKey;
import java.util.Base64;
import java.util.Objects;
import java.util.logging.Logger;

public class Node {
    private static final Logger LOGGER = Logger.getLogger(Node.class.getName());
    private final String id;
    private String ip;
    private String publicIp;
    private int port;
    private final PublicKey publicKey;
    private static ObjectMapper objectMapper;

    public Node(PublicKey publicKey, int port) {
        this.publicKey = publicKey;
        this.id = generateId();
        this.port = port;

        objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule("NodeSerializer", new Version(1, 0, 0, null, null, null));
        module.addSerializer(Node.class, new NodeSerializer());
        module.addDeserializer(Node.class, new NodeDeserializer());
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

    public static Node getNodeFromJSONSting(String jsonString) {
        Node node = null;
        try {
            node = objectMapper.readValue(jsonString, Node.class);
        } catch (JsonProcessingException e) {
            LOGGER.warning("Error while generating Node object from JSON string");
            e.printStackTrace();
        }

        return node;
    }

    public String getJSONString() {
        String jsonString = "";
        try {
            jsonString = objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            LOGGER.warning("Error while generating JSON String");
            e.printStackTrace();
        }
        return jsonString;
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

    @Override
    public String toString() {
        return "Node{" +
                "id='" + id + '\'' +
                ", ip='" + ip + '\'' +
                ", publicIp='" + publicIp + '\'' +
                ", port=" + port +
                ", publicKey=" + Base64.getEncoder().encodeToString(publicKey.getEncoded()) +
                '}';
    }
}


