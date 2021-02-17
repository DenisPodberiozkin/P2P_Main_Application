package User.CommunicationUnit.Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.Callable;

public class OutboundSession implements Callable<String> {

    private final Socket socket;
    BufferedReader reader;
    private int id;
    private boolean isRunning;

    public OutboundSession(Socket socket, BufferedReader reader) {
        this.socket = socket;
        this.isRunning = true;
        this.reader = reader;
    }

    @Override
    public String call() throws IOException {
        String response;
        do {

            response = reader.readLine();
            System.out.println(response);
            String[] tokens = response.split(" ");
            processTokens(tokens);

        } while (isRunning);


        return response;
    }


    private void stopSession() {
        this.isRunning = false;
    }

    private void processTokens(String[] tokens) {
        //TODO response protocol;

    }

    public int getId() {
        return id;
    }
}
