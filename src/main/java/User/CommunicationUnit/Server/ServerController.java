package User.CommunicationUnit.Server;

import javafx.beans.property.SimpleIntegerProperty;

public class ServerController implements IServerController {
    private static ServerController instance;
    private Server server;

    public static ServerController getInstance() {
        if (instance == null) {
            instance = new ServerController();
        }
        return instance;
    }

    @Override
    public void startServer(int port) {
        server = new Server(port);
        new Thread(server).start();
    }

    @Override
    public SimpleIntegerProperty getServerPortProperty() {
        return server.getPortProperty();
    }
}
