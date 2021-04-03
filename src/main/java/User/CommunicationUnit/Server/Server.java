package User.CommunicationUnit.Server;

import javafx.beans.property.SimpleIntegerProperty;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

public class Server implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(Server.class.getName());
    private final SimpleIntegerProperty port = new SimpleIntegerProperty();
    private ServerSocket serverSocket;
    private boolean isRunning;

    public Server(int port) {
        openServerSocket(port);
        this.isRunning = true;
    }

    @Override
    public void run() {
        isRunning = true;
        LOGGER.info("User server has starter");
        while (isRunning) {
            try {
                Socket clientSocket = serverSocket.accept();
                InboundConnection inboundConnection = new InboundConnection(clientSocket, this);
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

    public int getPort() {
        return port.get();
    }


    public void stopServer() {
        try {
            serverSocket.close();
        } catch (IOException ioException) {
            LOGGER.warning("Unable to stop server. Reason - " + ioException.getMessage());
        }
        this.isRunning = false;
        LOGGER.info("User server has stopped");
    }


}
