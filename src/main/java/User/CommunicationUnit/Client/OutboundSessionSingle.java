package User.CommunicationUnit.Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;

public class OutboundSessionSingle extends OutboundSession {

    public OutboundSessionSingle(Socket socket, BufferedReader reader) {
        super(socket, reader);
    }

    @Override
    public String call() throws IOException {
        return reader.readLine();
    }
}
