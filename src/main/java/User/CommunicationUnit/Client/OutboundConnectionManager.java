package User.CommunicationUnit.Client;

import GUI.ControllerFactory;
import User.NodeManager.Node;

import java.io.IOException;

public class OutboundConnectionManager {
    private static OutboundConnectionManager instance;

    public static OutboundConnectionManager getInstance() {
        if (instance == null) {
            instance = new OutboundConnectionManager();
        }
        return instance;
    }

    public OutboundConnection createConnection(String ip, int port) throws IOException {
        OutboundConnection connection = new OutboundConnection(ip, port);
        connection.createConnection();
        return connection;
    }

    public OutboundConnection createConnection(String ip, int port, Node assignedNode) throws IOException {
        OutboundConnection connection = new OutboundConnection(ip, port, assignedNode);
        connection.createConnection();
        return connection;
    }


    public void closeConnection(OutboundConnection outboundConnection) {
        ControllerFactory.getDebuggerController().removeOutboundConnection(outboundConnection);
    }

}
