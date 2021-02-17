package User.CommunicationUnit.Client;

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
    public OutboundConnection connect(String ip, int port, boolean isSingle) {
        return OutboundConnectionManager.getInstance().createConnection(ip, port, isSingle);
    }

    @Override
    public void sendMessage(OutboundConnection connection, String message) {
        connection.sendMessage(message);
    }


    @Override
    public void closeConnection(OutboundConnection connection) {
        manager.closeConnection(connection);
    }

    @Override
    public String getLastResponse(OutboundConnection connection) {
        return manager.getLastResponse(connection);
    }
}
