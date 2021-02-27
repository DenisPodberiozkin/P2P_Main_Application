package User.CommunicationUnit.Client;

import User.CommunicationUnit.MessageReader;

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
                LOGGER.info("Message " + message + " was received from " + connection.getIp() + ":" + connection.getPort());
                String[] tokens = message.split(" ");
                int sessionID = Integer.parseInt(tokens[0]);
                OutboundSession session = connection.getSession(sessionID);
                if (session != null) {
                    LOGGER.info("Sending message to Outbound session " + sessionID);
                    session.notifyReply(message);
                }

            }
        } catch (IOException e) {
            LOGGER.warning("Connection was closed by host. Reason: " + e.toString());
        } finally {
            try {
                connection.closeConnection();
            } catch (IOException e) {
                LOGGER.severe("Error while closing connection");
                e.printStackTrace();
            }
        }
    }

    public void stopReceiver() {
        this.isRunning = false;
    }
}
