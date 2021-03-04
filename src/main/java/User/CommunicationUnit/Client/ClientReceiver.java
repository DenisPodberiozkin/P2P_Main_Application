package User.CommunicationUnit.Client;

import User.CommunicationUnit.MessageReader;
import User.NodeManager.User;

import java.io.IOException;
import java.util.logging.Logger;

public class ClientReceiver implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(ClientReceiver.class.getName());
    private final MessageReader reader;
    private final OutboundConnection connection;
    private boolean isRunning;

    public ClientReceiver(MessageReader reader, OutboundConnection connection) {
        this.reader = reader;
        this.connection = connection;
        this.isRunning = true;
    }

    @Override
    public void run() {
        try {
            while (isRunning) {
                String message = reader.readLine();
                if (message != null) {
                    LOGGER.config("Message " + message + " was received from " + connection.getIp() + ":" + connection.getPort());
                    String[] tokens = message.split(" ");
                    int sessionID = Integer.parseInt(tokens[0]);
                    OutboundSession session = connection.getSession(sessionID);
                    if (session != null) {
                        LOGGER.config("Sending message to Outbound session " + sessionID);
                        session.notifyReply(message);
                    }
                } else {
                    throw new IOException("Received null");
                }


            }

        } catch (IOException e) {
            if (!e.getMessage().equals("Socket closed")) {
                LOGGER.warning("Connection was closed by host. Reason: " + e.toString());
//                try {
                User.getInstance().removeNodeFromTableAndDisconnect(connection.getAssignedNode());
//                    connection.closeConnection();
//                } catch (IOException ioException) {
//                    LOGGER.severe("Error while closing connection");
//                    ioException.printStackTrace();
//                }
            }

        }
    }

    public void stopReceiver() {
        this.isRunning = false;
    }
}
