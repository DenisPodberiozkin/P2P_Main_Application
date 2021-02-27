package User.CommunicationUnit;

import java.io.OutputStream;
import java.io.PrintWriter;

public class SynchronisedWriter extends PrintWriter {

    public SynchronisedWriter(OutputStream out, boolean autoFlush) {
        super(out, autoFlush);
    }

    public synchronized void sendReply(int clientSessionId, String message) {
        String reply = clientSessionId + " " + message;
        //TODO encryption
        System.out.println("TODO encryption");
        super.println(reply);

    }
}
