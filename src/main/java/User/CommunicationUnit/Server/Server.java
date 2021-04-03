package User.CommunicationUnit.Server;

import javafx.beans.property.SimpleIntegerProperty;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.logging.Logger;

public class Server implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(Server.class.getName());
    private final SimpleIntegerProperty port = new SimpleIntegerProperty();
    private final LinkedList<InboundConnection> inboundConnections;
    private String localServerIP;
    private String publicServerIP;
    private ServerSocket serverSocket;
    private boolean isRunning;

    public Server(int port) {
        this.inboundConnections = new LinkedList<>();
        openServerSocket(port);
        System.out.println(serverSocket);
        this.isRunning = true;
    }

    @Override
    public void run() {
        isRunning = true;
        LOGGER.info("User server has starter");
        while (isRunning) {
            try {
                System.out.println(serverSocket);
                Socket clientSocket = serverSocket.accept();
                InboundConnection inboundConnection = new InboundConnection(clientSocket, this);
                inboundConnections.add(inboundConnection);
                inboundConnection.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void openServerSocket(int port) {
        try {
            this.serverSocket = new ServerSocket(port);
            this.port.set(serverSocket.getLocalPort());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public SimpleIntegerProperty getPortProperty() {
        return port;
    }

    public void removeConnection(InboundConnection connection) {
        inboundConnections.remove(connection);
    }

    public void stopServer() {
        this.isRunning = false;
    }


}
