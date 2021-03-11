package User.CommunicationUnit.Server;

import Encryption.DH;
import Encryption.EncryptionController;
import Encryption.IEncryptionController;
import User.CommunicationUnit.SynchronisedWriter;
import User.NodeManager.Node;
import User.NodeManager.User;

import javax.crypto.SecretKey;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.Base64;
import java.util.Deque;
import java.util.logging.Logger;

public class InboundSession implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(InboundSession.class.getName());
    private static int GLOBAL_ID = 0;
    private final IEncryptionController encryptionController = EncryptionController.getInstance();
    private final int id;
    private final int clientSessionId;
    private final SynchronisedWriter writer;
    private final InboundConnection connection;
    private InboundTokens request;
    private String[] tokens;


    public InboundSession(String[] tokens, SynchronisedWriter writer, InboundConnection connection) {
        this.writer = writer;
        this.connection = connection;
        this.id = GLOBAL_ID++;
        this.clientSessionId = Integer.parseInt(tokens[0]);
        this.tokens = Arrays.stream(tokens, 1, tokens.length).toArray(String[]::new);
    }

    @Override
    public void run() {
        try {
            final SecretKey sk = connection.getSecretKey();
            if (sk != null) {
                tokens = encryptionController.decryptStringByAES(sk, tokens[0]).split(" ");
            }
            request = validateRequest(tokens[0]);

            if (request != null) {

                if (request == InboundTokens.PING) {
                    LOGGER.config("Inbound Session " + id + " was created to process request "
                            + request.getToken() + " from " + connection.getIp() + ":" + connection.getPort());
                } else {
                    LOGGER.info("Inbound Session " + id + " was created to process request "
                            + request.getToken() + " from " + connection.getIp() + ":" + connection.getPort());
                }


                final String requestToken = request.getToken();
                String response;
                final User user = User.getInstance();
                switch (request) {
                    case HAS_NEIGHBOURS:
                        if (user.hasNeighbours()) {
                            response = requestToken + " 1";
                        } else {
                            response = requestToken + " 0";
                        }
                        send(response);
                        break;
                    case PING:
                        connection.getHeartBeatManager().pingReceived();
                        response = requestToken + " OK";
                        send(response);
                        break;
                    case FIND:
                        String lookUpId = this.tokens[1];
                        response = user.findNode(lookUpId);
                        send(response);
                        break;
                    case NEW_PREDECESSOR_NOTIFICATION:
                        Node candidateNode = Node.getNodeFromJSONSting(tokens[1]);
                        boolean isNewPredecessorUpdated = user.checkAndUpdateNewPredecessor(candidateNode);
                        String status = isNewPredecessorUpdated ? " ACCEPTED" : " DECLINED";
                        response = requestToken + status;
                        send(response);
                        break;
                    case GET_PREDECESSOR:
                        Node predecessor = user.getPredecessor();
                        if (predecessor != null) {
                            String predecessorJOSN = user.getPredecessor().getJSONString();
                            response = requestToken + " " + predecessorJOSN;
                        } else {
                            response = requestToken + " null";
                        }
                        send(response);
                        break;
                    case GET_SUCCESSORS_QUEUE:
                        final Deque<Node> successorsQueue = user.getSuccessorsQueue();
                        StringBuilder messageBuilder = new StringBuilder();
                        for (Node node : successorsQueue) {
                            messageBuilder.append(node.getJSONString()).append(" ");
                        }
                        response = requestToken + " " + messageBuilder.toString();
                        send(response);
                        break;
                    case TRANSFER_MESSAGE:
                        String receiverId = tokens[1];

                        String payload = tokens[2] + " " + tokens[3];
                        response = requestToken + " " + user.transferMessage(receiverId, payload);
                        send(response);
                        break;
                    case CREATE_SECURE_CHANNEL:
                        boolean isSecure = true;
                        SecretKey secretKey = null;
                        try {
                            final String receivedPublicKey64 = tokens[1];
                            final byte[] receivedPublicKeyData = Base64.getDecoder().decode(receivedPublicKey64);
                            final PublicKey receivedPublicKey = DH.getDHPublicKeyFromData(receivedPublicKeyData);
                            DH dh = new DH();
                            final PublicKey publicKeyToSend = dh.initReceiver(receivedPublicKey);
                            secretKey = dh.initSecretKey(receivedPublicKey);
                            final String publicKeyToSend64 = Base64.getEncoder().encodeToString(publicKeyToSend.getEncoded());
                            response = requestToken + " " + publicKeyToSend64;
                        } catch (GeneralSecurityException e) {
                            e.printStackTrace();
                            LOGGER.warning(" ERROR. Unable to create secure channel in connection "
                                    + connection.getIp() + ":" + connection.getPort() + " Reason " + e.toString());
                            response = requestToken + " ERROR. Unable to create secure channel in connection "
                                    + connection.getIp() + ":" + connection.getPort() + " Reason " + e.toString();
                            isSecure = false;
                        }

                        send(response);
                        connection.setSecretKey(secretKey);
                        if (!isSecure) {
                            connection.closeConnection();
                        }
                        break;
                    case CREATE_SECURE_MESSAGE_SESSION:

                        final String resId = tokens[1];
                        final long messageSessionId = Long.parseLong(tokens[2]);
                        final String publicKey64 = tokens[3];
                        String result = user.transferPublicKey(resId, publicKey64, messageSessionId);
                        response = requestToken + " " + result;
                        send(response);
                        break;
                    default:
                        LOGGER.warning("Unexpected value: " + request);
                        response = "ERROR : Unexpected value: " + request;
                        send(response);
                        break;
                }

                if (response.contains(InboundTokens.PING.getToken())) {
                    LOGGER.config("Inbound Session " + id + " sends reply " + response + " in response to "
                            + requestToken + " to " + connection.getIp() + ":" + connection.getPort());
                } else {
                    LOGGER.info("Inbound Session " + id + " sends reply " + response + " in response to "
                            + requestToken + " to " + connection.getIp() + ":" + connection.getPort());
                }
            } else {
                send("ERROR : Unexpected token");
            }


        } finally {
            connection.closeSession(this.id);

        }


    }

    public int getId() {
        return id;
    }

    public InboundTokens getRequest() {
        return request;
    }

    private InboundTokens validateRequest(String value) {
        try {
            return InboundTokens.findByValue(value);
        } catch (IllegalArgumentException e) {
            LOGGER.warning("Unexpected token " + value + " received");
            return null;
        }
    }

    private void send(String reply) {
        final SecretKey secretKey = connection.getSecretKey();
        String response;
        if (secretKey != null) {
            response = encryptionController.encryptStringByAES(secretKey, reply);
        } else {
            response = reply;
        }
        writer.sendMessage(clientSessionId, response);

    }
}
