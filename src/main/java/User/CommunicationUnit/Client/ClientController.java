package User.CommunicationUnit.Client;

import java.io.IOException;
import java.util.concurrent.FutureTask;

public class ClientController implements IClientController {

    private static ClientController instance;
    private final OutboundConnectionManager manager;

    public ClientController() {
        this.manager = OutboundConnectionManager.getInstance();
    }

    public static ClientController getInstance() {
        if (instance == null) {
            instance = new ClientController();
        }
        return instance;
    }

    @Override
    public OutboundConnection connect(String ip, int port) throws IOException {
        return OutboundConnectionManager.getInstance().createConnection(ip, port);
    }

    @Override
    public FutureTask<String> sendMessage(OutboundConnection connection, String message) {
        return connection.sendMessage(message);
    }


}
