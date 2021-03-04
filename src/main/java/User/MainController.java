package User;

import User.CommunicationUnit.Client.ClientController;
import User.CommunicationUnit.Client.IClientController;
import User.CommunicationUnit.Client.OutboundConnection;
import User.CommunicationUnit.Server.IServerController;
import User.CommunicationUnit.Server.ServerController;
import User.Database.DAO.UserDAO;
import User.Database.DataBaseController;
import User.Database.IDataBaseController;
import User.Encryption.EncryptionController;
import User.Encryption.IEncryptionController;
import User.NodeManager.Node;
import User.NodeManager.User;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

public class MainController implements IMainController {
    private static final Logger LOGGER = Logger.getLogger(MainController.class.getName());
    private static MainController instance;
    private final IClientController clientController;
    private final IDataBaseController dataBaseController = DataBaseController.getInstance();
    private final IEncryptionController encryptionController = EncryptionController.getInstance();
    private final IServerController serverController = ServerController.getInstance();


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

            serverController.startServer(user.getPort());

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public void connectToRing() {
        User user = new User(); //TODO delete later
        System.out.println("Inside");
        try (OutboundConnection serverConnection = clientController.connect(ConnectionsData.getLocalServerIp(), ConnectionsData.getLocalServerPort())) {

//                User user = User.getInstance(); //TODO uncomment later
            Node lastConnectedNode = clientController.getLastConnectedNode(serverConnection);
            if (user != null) {
                if (lastConnectedNode != null) {
                    try {
                        lastConnectedNode.connectToNode(false);
                        if (lastConnectedNode.hasNeighbours()) {
                            Node successorNode = Node.getNodeFromJSONSting(lastConnectedNode.lookUp(user.getId()).get());
                            lastConnectedNode.closeConnection();
                            user.join(successorNode);
                        } else {
                            user.join(lastConnectedNode);
                        }
                    } catch (InterruptedException | ExecutionException interruptedException) {
                        interruptedException.printStackTrace();
                        LOGGER.warning("Unable to receive reply from FIND message session. Reason: " + interruptedException.toString());
                    }
                } else {
                    user.join();
                }
            } else {
                LOGGER.warning("User undefined");
            }
            clientController.sendLastNodeToServer(serverConnection, user);
//
//
//                //TODO start checks
//
//                System.out.println(lastResponse);


        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }


}
