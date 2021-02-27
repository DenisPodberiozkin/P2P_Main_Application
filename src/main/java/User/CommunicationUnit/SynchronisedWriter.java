package User.CommunicationUnit;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.logging.Logger;

public class SynchronisedWriter extends PrintWriter {
    private static final Logger LOGGER = Logger.getLogger(SynchronisedWriter.class.getName());

    public SynchronisedWriter(OutputStream out, boolean autoFlush) {
        super(out, autoFlush);
    }

    public synchronized void sendMessage(int clientSessionId, String message) {
        String reply = clientSessionId + " " + message;
        //TODO encryption
        LOGGER.info("Message " + reply + " was sent");
        super.println(reply);

    }
}
