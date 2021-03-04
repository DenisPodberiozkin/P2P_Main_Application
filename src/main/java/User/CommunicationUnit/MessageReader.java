package User.CommunicationUnit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.logging.Logger;

public class MessageReader extends BufferedReader {
    private static final Logger LOGGER = Logger.getLogger(MessageReader.class.getName());

    public MessageReader(Reader in) {
        super(in);
    }

    @Override
    public String readLine() throws IOException {
        String line = super.readLine();
        //TODO decryption
        if (line != null) {
            LOGGER.config("Message " + line + " was received");
        }
        return line;
    }
}
