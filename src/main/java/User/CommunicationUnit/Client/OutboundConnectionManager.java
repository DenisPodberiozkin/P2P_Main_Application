package User.CommunicationUnit.Client;

import java.io.IOException;
import java.util.LinkedList;

public class OutboundConnectionManager {
    private static OutboundConnectionManager instance;
    private final LinkedList<OutboundConnection> outboundConnections;

    public OutboundConnectionManager() {
        outboundConnections = new LinkedList<>();
    }

    public static OutboundConnectionManager getInstance() {
        if (instance == null) {
            instance = new OutboundConnectionManager();
        }
        return instance;
    }

    public OutboundConnection createConnection(String ip, int port) throws IOException {
        OutboundConnection connection = new OutboundConnection(ip, port);
        connection.createConnection();
        outboundConnections.add(connection);
        return connection;
    }


    public void closeConnection(OutboundConnection outboundConnection) {
        outboundConnections.remove(outboundConnection);
    }

}
