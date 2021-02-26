package User.CommunicationUnit.Client;

import java.io.IOException;
import java.util.concurrent.FutureTask;

public interface IClientController {

    OutboundConnection connect(String ip, int port) throws IOException;

    FutureTask<String> sendMessage(OutboundConnection connection, String message);

}