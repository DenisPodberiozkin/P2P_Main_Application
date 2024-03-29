package User.CommunicationUnit.Client;

import Encryption.DH;
import User.CommunicationUnit.Server.InboundTokens;
import User.NodeManager.Node;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RejectedExecutionException;
import java.util.logging.Logger;

public class ClientController implements IClientController {

    private static final Logger LOGGER = Logger.getLogger(ClientController.class.getName());
    private static ClientController instance;

    public ClientController() {
    }

    public static ClientController getInstance() {
        if (instance == null) {
            instance = new ClientController();
        }
        return instance;
    }

    @Override
    public OutboundConnection connect(String ip, int port) throws IOException {
        return OutboundConnectionManager.getInstance().createConnection(ip, port);
    }

    @Override
    public OutboundConnection connect(String ip, int port, Node assignedNode) throws IOException {
        return OutboundConnectionManager.getInstance().createConnection(ip, port, assignedNode);
    }

    private FutureTask<String> sendMessage(OutboundConnection connection, String message, boolean isEncrypted) {
        return connection.sendMessage(message, isEncrypted);
    }

    private String[] verifyAndCleanTokens(String message, String token) {
        String[] tokens = message.split(" ");
        if (tokens.length > 1) {
            if (tokens[0].equals(token)) {
                return Arrays.stream(tokens, 1, tokens.length).toArray(String[]::new);
            }
            LOGGER.warning("Illegal token - " + tokens[0] + " Expected - " + token);
            return null;
        } else {
            LOGGER.warning("Message without token or without data received. Message " + message);
            return new String[0];
        }

    }


    @Override
    public Node getLastConnectedNode(OutboundConnection connection) {
        try {
            String token = OutboundTokens.GET_LAST_NODE.getToken();
            String reply = sendMessage(connection, token, true).get();
            String lastConnectedNodeJSON = Objects.requireNonNull(verifyAndCleanTokens(reply, "LN"))[0];
            if (lastConnectedNodeJSON != null) {
                return Node.getNodeFromJSONSting(lastConnectedNodeJSON);
            }
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.warning("Unable to receive reply from GETLN message session. Reason: " + e.toString());
        }

        return null;
    }

    @Override
    public boolean hasNeighbours(OutboundConnection connection) {
        try {
            String token = InboundTokens.HAS_NEIGHBOURS.getToken();
            String reply = sendMessage(connection, token, true).get();
            int result = Integer.parseInt(Objects.requireNonNull(verifyAndCleanTokens(reply, token))[0]);
            return result == 1;
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.warning("Unable to receive reply from HN message session. Reason: " + e.toString());

        }
        return false;
    }

    @Override
    public Node getNodeInformation(OutboundConnection connection) {
        try {
            final String token = InboundTokens.GET_NODE_INFORMATION.getToken();
            final String reply = sendMessage(connection, token, true).get();
            return Node.getNodeFromJSONSting(Objects.requireNonNull(verifyAndCleanTokens(reply, token))[0]);
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.warning("Unable to receive reply from GNI message session. Reason: " + e.toString());
        }
        return null;
    }

    @Override
    public String lookUp(OutboundConnection connection, String id) {
        String reply = "NF";
        try {
            String token = InboundTokens.FIND.getToken();
            String message = token + " " + id;
            reply = sendMessage(connection, message, true).get();
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.warning("Unable to receive reply from FIND message session. Reason: " + e.toString());
        }
        return reply;
    }

    @Override
    public void sendLastNodeToServer(OutboundConnection connection, Node node) {
        String token = OutboundTokens.SET_LAST_NODE.getToken();
        String message = token + " " + node.getJSONString();
        sendMessage(connection, message, true);
    }

    @Override
    public void sendNotificationAboutNewPredecessor(OutboundConnection connection, Node node) {
        final String token = InboundTokens.NEW_PREDECESSOR_NOTIFICATION.getToken();
        final String message = token + " " + node.getJSONString();
        sendMessage(connection, message, true);
    }

    @Override
    public Node getPredecessor(OutboundConnection connection) throws RejectedExecutionException {
        try {
            String token = InboundTokens.GET_PREDECESSOR.getToken();
            String reply = sendMessage(connection, token, true).get();
            String predecessorJSON = Objects.requireNonNull(verifyAndCleanTokens(reply, token))[0];
            return Node.getNodeFromJSONSting(predecessorJSON);
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.warning("Unable to receive reply from GET PREDECESSOR message session. Reason: " + e.toString());
        } catch (RejectedExecutionException e) {
            LOGGER.warning("Connection is closed!");
            throw new RejectedExecutionException();
        }

        return null;

    }

    @Override
    public Deque<Node> getSuccessorsQueue(OutboundConnection connection) {
        try {
            final String token = InboundTokens.GET_SUCCESSORS_QUEUE.getToken();
            String reply = sendMessage(connection, token, true).get();
            String[] tokens = verifyAndCleanTokens(reply, token);
            Deque<Node> successorsQueue = new LinkedList<>();
            if (tokens != null) {
                if (tokens.length > 0) {
                    for (String jsonNode : tokens) {
                        successorsQueue.offer(Node.getNodeFromJSONSting(jsonNode));
                    }
                    return successorsQueue;
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.warning("Unable to receive reply from GET SUCCESSORS LIST message session. Reason: " + e.toString());
        }

        return null;
    }

    @Override
    public void removeUnreachableLastConnectedNode(OutboundConnection connection, String nodeJSON) {
        try {
            String token = OutboundTokens.REMOVE_UNREACHABLE_LAST_CONNECTED_NODE.getToken();
            String message = token + " " + nodeJSON;
            sendMessage(connection, message, true).get();
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.warning("Unable to receive reply from REMOVE UNREACHABLE LAST CONNECTED NODE message session. Reason: " + e.toString());
        }

    }

    @Override
    public String transferMessage(OutboundConnection connection, String receiverId, String payload) {
        try {
            String token = InboundTokens.TRANSFER_MESSAGE.getToken();
            String message = token + " " + receiverId + " " + payload;
            String reply = sendMessage(connection, message, true).get();
            String[] tokens = verifyAndCleanTokens(reply, token);
            if (tokens != null) {
                return tokens[0];
            }
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.warning("Unable to receive reply from TRANSFER MESSAGE message session. Reason: " + e.toString());
            return "NF";
        }

        return "NF";

    }

    @Override
    public PublicKey exchangePublicKeys(OutboundConnection connection, PublicKey publicKeyToSend) throws ExecutionException, InterruptedException, IllegalArgumentException, GeneralSecurityException {
        String publicKeyToSend64 = Base64.getEncoder().encodeToString(publicKeyToSend.getEncoded());
        final String token = InboundTokens.CREATE_SECURE_CHANNEL.getToken();
        final String message = token + " " + publicKeyToSend64;
        String response = sendMessage(connection, message, false).get();
        String[] tokens = verifyAndCleanTokens(response, token);
        if (tokens != null) {
            String receivedPublicKey64 = tokens[0];
            final byte[] receivedPublicKeyData = Base64.getDecoder().decode(receivedPublicKey64);
            return DH.getDHPublicKeyFromData(receivedPublicKeyData);
        } else {
            throw new IllegalArgumentException("Unexpected Token");
        }
    }


    @Override
    public String transferPublicKey(OutboundConnection connection, String publicKeyToSend64, String receiverId, long messageSessionId) {
        try {
            final String token = InboundTokens.CREATE_SECURE_MESSAGE_SESSION.getToken();
            final String payload = messageSessionId + " " + publicKeyToSend64;
            final String message = token + " " + receiverId + " " + payload;
            String reply = sendMessage(connection, message, true).get();
            if (reply.contains("Error")) {
                return reply;
            }
            String[] tokens = verifyAndCleanTokens(reply, token);
            if (tokens != null) {
                return tokens[0] + " " + tokens[1];
            }
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.warning("Unable to receive reply from TRANSFER PUBLIC KEY message session. Reason: " + e.toString());
            return "NF";
        }
        return "NF";
    }
}
