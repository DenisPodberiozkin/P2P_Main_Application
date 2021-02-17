package User;

import User.CommunicationUnit.Client.ClientController;
import User.CommunicationUnit.Client.IClientController;
import User.CommunicationUnit.Client.OutboundConnection;
import User.Database.DataBaseController;
import User.Encryption.EncryptionController;

import java.security.KeyPair;
import java.sql.Connection;

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
    public void createAccount(String password) {
        DataBaseController dataBaseController = DataBaseController.getInstance();
        Connection connection = dataBaseController.connectToDatabase();
        EncryptionController encryptionController = EncryptionController.getInstance();
        KeyPair keyPair = encryptionController.generateKeyPair();


    }

    @Override
    public void loginToAccount() {

    }

    @Override
    public void connectToRing() {
        OutboundConnection connection = clientController.connect("localhost", 4444, true);
        if (connection != null) {
            System.out.println("Inside");
            clientController.sendMessage(connection, "GETLN");
            String lastResponse = clientController.getLastResponse(connection);

            clientController.closeConnection(connection);

            System.out.println(lastResponse);
        } else {
            System.err.println("Could not connect to Local Server ");
        }


    }
}
