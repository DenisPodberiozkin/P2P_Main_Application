package User.CommunicationUnit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

public class MessageReader extends BufferedReader {

    public MessageReader(Reader in) {
        super(in);
    }

    @Override
    public String readLine() throws IOException {
        String line = super.readLine();
        //TODO decryption
        System.out.println("TODO decryption");
        return line;
    }
}
