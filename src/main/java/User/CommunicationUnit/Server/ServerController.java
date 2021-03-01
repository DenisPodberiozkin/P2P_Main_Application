package User.CommunicationUnit.Server;

public class ServerController implements IServerController {
    private static ServerController instance;

    public static ServerController getInstance() {
        if (instance == null) {
            instance = new ServerController();
        }
        return instance;
    }

    @Override
    public void startServer(int port) {
        Server server = new Server(port);
        new Thread(server).start();
    }
}
