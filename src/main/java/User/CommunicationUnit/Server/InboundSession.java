package User.CommunicationUnit.Server;

import User.CommunicationUnit.SynchronisedWriter;
import User.NodeManager.Node;
import User.NodeManager.User;

import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

public class InboundSession implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(InboundSession.class.getName());
    private static int GLOBAL_ID = 0;
    private final InboundTokens request;
    private final int id;
    private final int clientSessionId;
    private final String[] tokens;
    private final SynchronisedWriter writer;
    private final InboundConnection connection;

    public InboundSession(String[] tokens, SynchronisedWriter writer, InboundConnection connection) {
        this.writer = writer;
        this.connection = connection;
        this.id = GLOBAL_ID++;
        this.clientSessionId = Integer.parseInt(tokens[0]);
        this.tokens = Arrays.stream(tokens, 1, tokens.length).toArray(String[]::new);
        this.request = validateRequest(this.tokens[0]);
    }

    @Override
    public void run() {
        if (request != null) {

            if (request == InboundTokens.PING) {
                LOGGER.config("Inbound Session " + id + " was created to process request " + request.getToken() + " from " + connection.getIp() + ":" + connection.getPort());
            } else {
                LOGGER.info("Inbound Session " + id + " was created to process request " + request.getToken() + " from " + connection.getIp() + ":" + connection.getPort());
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
                    writer.sendMessage(clientSessionId, response);
                    break;
                case PING:
                    connection.getHeartBeatManager().pingReceived();
                    response = requestToken + " OK";
                    writer.sendMessage(clientSessionId, response);
                    break;
                case FIND:
                    try {
                        String lookUpId = this.tokens[1];
                        String findResult = user.lookUp(lookUpId).get();
                        String[] findResultTokens = findResult.split(" ");
                        final int length = findResultTokens.length;
                        if ((length > 1 && findResultTokens[0].equals("FR")) || length == 1) {
                            response = length == 1 ? findResultTokens[0] : findResultTokens[1];
                        } else {
                            LOGGER.warning("Unexpected token in FIND RESULT - " + findResultTokens[0] + " , was expected \"FR\"");
                            response = "ERROR : Unexpected token";
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        LOGGER.warning("Unable to receive reply form FIND message session. Reason: " + e.toString());
                        response = "ERROR : Unable to receive reply form FIND message session. Reason: " + e.toString();
                    }
                    writer.sendMessage(clientSessionId, response);
                    break;
                case NEW_PREDECESSOR_NOTIFICATION:
                    Node candidateNode = Node.getNodeFromJSONSting(tokens[1]);
                    boolean isNewPredecessorUpdated = user.checkAndUpdateNewPredecessor(candidateNode);
                    String status = isNewPredecessorUpdated ? " ACCEPTED" : " DECLINED";
                    response = requestToken + status;
                    writer.sendMessage(clientSessionId, response);
                    break;
                case GET_PREDECESSOR:
                    Node predecessor = user.getPredecessor();
                    if (predecessor != null) {
                        String predecessorJOSN = user.getPredecessor().getJSONString();
                        response = requestToken + " " + predecessorJOSN;
                    } else {
                        response = requestToken + " null";
                    }
                    writer.sendMessage(clientSessionId, response);
                    break;
                case GET_SUCCESSORS_QUEUE:
                    final Queue<Node> successorsQueue = user.getSuccessorsQueue();
                    StringBuilder messageBuilder = new StringBuilder();
                    for (Node node : successorsQueue) {
                        messageBuilder.append(node.getJSONString()).append(" ");
                    }
                    response = requestToken + " " + messageBuilder.toString();
                    writer.sendMessage(clientSessionId, response);
                    break;
                default:
                    LOGGER.warning("Unexpected value: " + request);
                    response = "ERROR : Unexpected value: " + request;
                    writer.sendMessage(clientSessionId, response);
                    break;
            }

            if (response.contains(InboundTokens.PING.getToken())) {
                LOGGER.config("Inbound Session " + id + " sends reply " + response + " in response to " + requestToken + " to " + connection.getIp() + ":" + connection.getPort());
            } else {
                LOGGER.info("Inbound Session " + id + " sends reply " + response + " in response to " + requestToken + " to " + connection.getIp() + ":" + connection.getPort());
            }
        } else {
            writer.sendMessage(clientSessionId, "ERROR : Unexpected token");
        }


        connection.closeSession(this.id);
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
}
