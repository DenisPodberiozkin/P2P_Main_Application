package User.CommunicationUnit.Client;

import java.io.BufferedReader;
import java.io.IOException;

public class ClientReceiver implements Runnable {
    private final BufferedReader reader;
    private final OutboundConnection connection;
    private boolean isRunning;

    public ClientReceiver(BufferedReader reader, OutboundConnection connection) {
        this.reader = reader;
        this.connection = connection;
        this.isRunning = true;
    }

    @Override
    public void run() {
        try {
            while (isRunning) {
                String message = reader.readLine();
                System.out.println(message + " received");
                String[] tokens = message.split(" ");
                int sessionID = Integer.parseInt(tokens[0]);
                OutboundSession session = connection.getSession(sessionID);
                if (session != null) {
                    session.notifyReply(message);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopReceiver() {
        this.isRunning = false;
    }
}
