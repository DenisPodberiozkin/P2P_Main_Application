package User.CommunicationUnit.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable {

    private final int port;
    private String localServerIP;
    private String publicServerIP;
    private ServerSocket serverSocket;

    public Server(int port) {
        this.port = port;
        openServerSocket();
    }

    @Override
    public void run() {
        while (true) {
            try {
                Socket senderSocket = serverSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void openServerSocket() {
        try {
            this.serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
