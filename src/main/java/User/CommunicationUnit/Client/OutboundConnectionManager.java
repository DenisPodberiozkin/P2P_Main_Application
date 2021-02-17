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

    public OutboundConnection createConnection(String ip, int port, boolean isSingle) {
        OutboundConnection connection = new OutboundConnection(ip, port);
        try {
            connection.createConnection(isSingle);
            outboundConnections.add(connection);
        } catch (IOException e) {
            System.out.println("Could not connect to " + ip + ":" + port);
            closeConnection(connection);
            return null;

        }
        return connection;
    }


    public void closeConnection(OutboundConnection outboundConnection) {
        try {
            outboundConnection.closeConnection();
            outboundConnections.remove(outboundConnection);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public String getLastResponse(OutboundConnection connection) {
        return connection.getSingleResponse();
    }
}
