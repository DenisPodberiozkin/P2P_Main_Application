package User;

import User.CommunicationUnit.Client.ClientController;
import User.CommunicationUnit.Client.IClientController;
import User.CommunicationUnit.Client.OutboundConnection;
import User.Database.DAO.UserDAO;
import User.Database.DataBaseController;
import User.Database.IDataBaseController;
import User.Encryption.EncryptionController;
import User.Encryption.IEncryptionController;
import User.NodeManager.User;

import javax.crypto.SecretKey;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.sql.Connection;
import java.sql.SQLException;

public class MainController implements IMainController {
    private static MainController instance;
    private final IClientController clientController;
    private final IDataBaseController dataBaseController = DataBaseController.getInstance();
    private final IEncryptionController encryptionController = EncryptionController.getInstance();

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
    public String createAccount(String password) {
        KeyPair keyPair = encryptionController.generateKeyPair();
        User user = new User(keyPair);
        String secretPassword = encryptionController.generateSecretPassword();
        SecretKey secretKey = encryptionController.generateAESKey(password, secretPassword);
        byte[] encryptedPrivateKey = encryptionController.encryptFileByAES(secretKey, user.getPrivateKey().getEncoded());
        try (Connection connection = dataBaseController.connectToDatabase()) {
            UserDAO userDAO = new UserDAO(connection);
            userDAO.addUser(user.getId(), user.getPublicKey().getEncoded(), encryptedPrivateKey);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }


        return secretPassword;
    }

    @Override
    public void loginToAccount(String password, String secretPassword) {
        try (Connection connection = dataBaseController.connectToDatabase()) {
            UserDAO userDAO = new UserDAO(connection);
            User user = userDAO.retrieveUserPublicData();

            byte[] encryptedPrivateKeyData = userDAO.retrieveEncryptedPrivateKey();

            SecretKey secretKey = encryptionController.generateAESKey(password, secretPassword);

            byte[] decryptedPrivateKeyData = encryptionController.decryptFileByAES(secretKey, encryptedPrivateKeyData);

            PrivateKey privateKey = encryptionController.getPrivateKeyFromBytes(decryptedPrivateKeyData);
            user.setPrivateKey(privateKey);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
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
