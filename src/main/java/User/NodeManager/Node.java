package User.NodeManager;

import User.CommunicationUnit.Client.ClientController;
import User.CommunicationUnit.Client.IClientController;
import User.CommunicationUnit.Client.OutboundConnection;
import User.Encryption.EncryptionController;
import User.Encryption.EncryptionUtil;
import User.JSON.NodeDeserializer;
import User.JSON.NodeSerializer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.IOException;
import java.security.PublicKey;
import java.util.Base64;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RejectedExecutionException;
import java.util.logging.Logger;

public class Node implements AutoCloseable {
    private static final Logger LOGGER = Logger.getLogger(Node.class.getName());
    private static ObjectMapper objectMapper;
    private final String id;
    private final PublicKey publicKey;
    private final IClientController clientController;
    private String ip;
    private String publicIp;
    private int port;
    private OutboundConnection connection;

    public Node(PublicKey publicKey, int port) {
        this.publicKey = publicKey;
        this.id = generateId();
        this.port = port;
        this.clientController = ClientController.getInstance();

        objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule("NodeSerializer", new Version(1, 0, 0, null, null, null));
        module.addSerializer(Node.class, new NodeSerializer());
        module.addDeserializer(Node.class, new NodeDeserializer());
        objectMapper.registerModule(module);
    }

    public Node(PublicKey publicKey, int port, OutboundConnection connection) {
        this(publicKey, port);
        this.connection = connection;
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

    public OutboundConnection getConnection() {
        return connection;
    }

    public void removeConnection() {
        this.connection = null;
    }

    public FutureTask<String> lookUp(String id) {
        return clientController.lookUp(connection, id);
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

    // Sends notification to the Node saying that we are its new predecessor
    public void notifyAboutNewPredecessor(User user) {
        if (connection != null) {
            clientController.sendNotificationAboutNewPredecessor(connection, user);
        } else {
            LOGGER.warning("Connection to node " + ip + ":" + port + " is not established to notify about new predecessor");
        }
    }

    public OutboundConnection connectToNode(boolean isPublicConnection) {
        String ip = isPublicConnection ? this.publicIp : this.ip;
        try {
            this.connection = clientController.connect(ip, port, this);
            return this.connection;
        } catch (IOException ioException) {
            LOGGER.warning("Unable to connect to node " + ip + ":" + port + " Reason: " + ioException.toString());
            ioException.printStackTrace();
        }
        return null;
    }

    protected boolean isConnected() {
        return connection != null;

    }

    public void closeConnection() {
        if (connection != null) {
            try {
                connection.closeConnection();
                connection = null;
            } catch (IOException ioException) {
                LOGGER.warning("Unable to disconnect from " + ip + ":" + port + " Reason: " + ioException.toString());
                ioException.printStackTrace();
            }
        }
    }

    protected Node getPredecessor() {
        if (connection != null) {
            try {
                return clientController.getPredecessor(connection);
            } catch (RejectedExecutionException e) {
                connection = connectToNode(false);
                return clientController.getPredecessor(connection);
            }
        } else {
            LOGGER.warning("Connection to node " + ip + ":" + port + " is not established to get predecessor");
        }
        return null;
    }

    public boolean hasNeighbours() {
        if (connection != null) {
            return clientController.hasNeighbours(connection);
        }
        LOGGER.warning("Connection to node " + ip + ":" + port + " is not established to check if it has neighbours");
        return false;
    }

    public Queue<Node> getSuccessorsQueue() {
        return clientController.getSuccessorsQueue(connection);
    }

    @Override
    public Node clone() {
        return getNodeFromJSONSting(getJSONString());
    }

    @Override
    public void close() throws Exception {
        closeConnection();
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


