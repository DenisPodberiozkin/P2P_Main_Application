package User.CommunicationUnit.Server;

public class ServerController implements IServerController {
    private static ServerController instance;

    public static ServerController getInstance() {
        if (instance == null) {
            instance = new ServerController();
        }
        return instance;
    }

}
