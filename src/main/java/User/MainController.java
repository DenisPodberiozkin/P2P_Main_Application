package User;

import User.CommunicationUnit.Client.ClientController;
import User.CommunicationUnit.Client.IClientController;
import User.CommunicationUnit.Client.OutboundConnection;

public class MainController implements IMainController {
    private static MainController instance;
    private final IClientController clientController;

    public MainController() {
        this.clientController = ClientController.getInstance();
    }

    public static MainController getInstance() {
        if (instance == null) {
            instance = new MainController();
        }
        return instance;
    }

    @Override
    public void connectToRing() {
        OutboundConnection connection = clientController.connect("localhost", 4444, true);
        clientController.sendMessage(connection, "GETLN");
        String lastResponse = clientController.getLastResponse(connection);

        clientController.closeConnection(connection);

        System.out.println(lastResponse);


    }
}
