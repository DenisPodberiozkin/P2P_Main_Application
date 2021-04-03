package User.NodeManager;

import Encryption.EncryptionController;
import Encryption.EncryptionUtil;
import User.CommunicationUnit.Client.ClientController;
import User.CommunicationUnit.Client.IClientController;
import User.CommunicationUnit.Client.OutboundConnection;
import User.JSON.NodeDeserializer;
import User.JSON.NodeSerializer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.IOException;
import java.security.PublicKey;
import java.util.Deque;
import java.util.Objects;
import java.util.concurrent.RejectedExecutionException;
import java.util.logging.Logger;

public class Node implements AutoCloseable {
	private static final Logger LOGGER = Logger.getLogger(Node.class.getName());
	private static ObjectMapper objectMapper;
	private final IClientController clientController = ClientController.getInstance();
	private String id;
	private PublicKey publicKey;
	private String ip;
	private int port;
	private OutboundConnection connection;

	public Node(PublicKey publicKey) {
		this.publicKey = publicKey;
		this.id = generateId();

		objectMapper = new ObjectMapper();
		SimpleModule module = new SimpleModule("NodeSerializer", new Version(1, 0, 0, null, null, null));
		module.addSerializer(Node.class, new NodeSerializer());
		module.addDeserializer(Node.class, new NodeDeserializer());
		objectMapper.registerModule(module);
	}


	public Node(String ip, int port) {
		this.ip = ip;
		this.port = port;
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

	public String findNode(String id) {
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

	public OutboundConnection connectToNode() throws IOException {
		this.connection = clientController.connect(ip, port, this);
		return this.connection;
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
				LOGGER.warning("Connection to node " + getIp() + ":" + getPort() + " is closed, trying to reconnect");
				try {
					connection = connectToNode();
					return clientController.getPredecessor(connection);
				} catch (IOException ioException) {
					LOGGER.warning("Reconnection to node " + getIp() + ":" + getPort() + " is failed");
				}
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

	public void initNodeInformation() {
		if (connection != null) {
			final Node node = clientController.getNodeInformation(connection);
			this.id = node.getId();
			this.publicKey = node.getPublicKey();

		}
		LOGGER.warning("Connection to node " + ip + ":" + port + " is not established to get its information");
	}

	public Deque<Node> getSuccessorsQueue() {
		return clientController.getSuccessorsQueue(connection);
	}

	public String transferMessage(String receiverId, String payload) {
		return clientController.transferMessage(connection, receiverId, payload);
	}

	public String transferPublicKey(String receiverId, String publicKey64, long messageSessionId) {
		return clientController.transferPublicKey(connection, publicKey64, receiverId, messageSessionId);
	}

	@Override
	public Node clone() {
		return getNodeFromJSONSting(getJSONString());
	}

	@Override
	public void close() {
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




}


