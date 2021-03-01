package User.CommunicationUnit.Server;

import User.CommunicationUnit.SynchronisedWriter;
import User.NodeManager.User;

import java.util.Arrays;
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
            LOGGER.info("Inbound Session " + id + " was created to process request " + request.getToken());
            final String requestToken = request.getToken();
            final String response;
            switch (request) {
                case HAS_NEIGHBOURS:
                    if (User.getInstance().hasNeighbours()) {
                        response = requestToken + " 1";
                    } else {
                        response = requestToken + " 0";
                    }
                    writer.sendMessage(clientSessionId, response);
                    break;
                case PING:
                    connection.getHeartBeatManager().pingReceived();
                    writer.sendMessage(clientSessionId, "PING OK");
                    break;
                default:
                    System.err.println("Unexpected value: " + request);
            }
        } else {
            writer.sendMessage(clientSessionId, "ERROR : Unexpected token");
        }


        connection.closeSession(this.id);
    }

    public int getId() {
        return id;
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
