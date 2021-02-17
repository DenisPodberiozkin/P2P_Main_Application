package User.CommunicationUnit.Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class OutboundConnection {
    private final String ip;
    private final int port;
    protected BufferedReader reader;
    private PrintWriter writer;
    private Socket socket;
    private OutboundSession session;
    private FutureTask<String> response;

    public OutboundConnection(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public void createConnection(boolean isSingle) throws IOException {
        socket = new Socket(ip, port);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new PrintWriter(socket.getOutputStream(), true);
        if (isSingle) {
            session = new OutboundSessionSingle(socket, reader);
        } else {
            session = new OutboundSession(socket, reader);
        }
        response = new FutureTask<>(session);
        new Thread(response).start();


    }

    public void sendMessage(String message) {
        System.out.println(message);
        writer.println(message);
    }

    public void closeConnection() throws IOException {
        if (reader != null) {
            reader.close();
        }
        if (writer != null) {
            writer.close();
        }
        if (socket != null) {
            socket.close();
        }

    }

    public OutboundSession getSession() {
        return session;
    }

    public String getSingleResponse() {
        try {
            if (session instanceof OutboundSessionSingle) {
                return response.get();
            } else {
                System.err.println("Session must be single");
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return "";
    }
}
