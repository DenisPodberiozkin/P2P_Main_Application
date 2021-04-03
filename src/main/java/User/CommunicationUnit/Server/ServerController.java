package User.CommunicationUnit.Server;

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
    public void stopServer() {
        if (server != null) {
            server.stopServer();
        }
    }

    @Override
    public Server getServer() {
        return server;
    }
}
