package User;

import Encryption.EncryptionController;
import Encryption.IEncryptionController;
import User.CommunicationUnit.Client.ClientController;
import User.CommunicationUnit.Client.IClientController;
import User.CommunicationUnit.Client.OutboundConnection;
import User.CommunicationUnit.Server.IServerController;
import User.CommunicationUnit.Server.ServerController;
import User.Database.DAO.UserDAO;
import User.Database.DataBaseController;
import User.Database.DatabaseExceptions.DatabaseInitException;
import User.Database.IDataBaseController;
import User.Database.UserDataBase;
import User.NodeManager.Node;
import User.NodeManager.User;
import User.Settings.ApplicationSettingsModel;
import User.Settings.ConnectionSettingsModel;

import javax.crypto.SecretKey;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.sql.Connection;
import java.sql.SQLException;
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
	public void initSettings() throws FileNotFoundException, SQLException {
		Connection connection = dataBaseController.connectToSettingsDatabase();
		ApplicationSettingsModel.initSettings(connection);
		ConnectionSettingsModel.initConnectionSettings(connection);
	}

	@Override
	public void createAccount(String password, String secretPassword, String username) throws IOException, SQLException, DatabaseInitException {
		KeyPair keyPair = encryptionController.generateRSAKeyPair();
		User user = new User(keyPair);
		SecretKey secretKey = encryptionController.generateAESKey(password, secretPassword);
		byte[] encryptedPrivateKey = encryptionController.encryptDataByAES(secretKey, user.getPrivateKey().getEncoded());

		UserDataBase userDataBase = dataBaseController.createUserDatabase(username);
		try (Connection connection = dataBaseController.connectToUserDatabase(username, userDataBase)) {
			dataBaseController.initUserDataBase(userDataBase, connection);
			UserDAO userDAO = new UserDAO(connection);
			userDAO.addUser(user.getId(), user.getPublicKey().getEncoded(), encryptedPrivateKey);
		}
	}

	@Override
	public void loginToAccount(String password, String secretPassword, String username) throws FileNotFoundException, SQLException, GeneralSecurityException {
		try (Connection connection = dataBaseController.connectToUserDatabase(username, new UserDataBase())) {
			UserDAO userDAO = new UserDAO(connection);
			User user = userDAO.retrieveUserPublicData();

			byte[] encryptedPrivateKeyData = userDAO.retrieveEncryptedPrivateKey();

			SecretKey secretKey = encryptionController.generateAESKey(password, secretPassword);

			byte[] decryptedPrivateKeyData = encryptionController.decryptDataByAES(secretKey, encryptedPrivateKeyData);

			PrivateKey privateKey = encryptionController.getPrivateKeyFromBytes(decryptedPrivateKeyData);
			user.setPrivateKey(privateKey);

			serverController.startServer(user.getPort());

		}
	}

	@Override
	public void connectToRing() {
		User user = new User(); //TODO delete later
		System.out.println("Inside");
		try (OutboundConnection serverConnection = clientController.connect(ConnectionsData.getLocalServerIp(), ConnectionsData.getLocalServerPort())) {

//                User user = User.getInstance(); //TODO uncomment later
			boolean isLastConnectedNodeReachable = false;
			boolean isLastConnectedNodePresent = false;
			do {
				Node lastConnectedNode = clientController.getLastConnectedNode(serverConnection);
				if (user != null) {
					if (lastConnectedNode != null) {
						isLastConnectedNodePresent = true;
						try {
							lastConnectedNode.connectToNode(false);
							isLastConnectedNodeReachable = true;
							if (lastConnectedNode.hasNeighbours()) {
								final String successorJson = lastConnectedNode.findNode(user.getId());
								if (successorJson.equals("NF")) {
									throw new IOException("Successor not found");
								}
								Node successorNode = Node.getNodeFromJSONSting(successorJson);
								lastConnectedNode.closeConnection();
								user.join(successorNode);
							} else {
								user.join(lastConnectedNode);
							}
						} catch (IOException ioException) {
							LOGGER.warning("Unable to connectToUserDatabase to last node. Reason " + ioException.toString());
							isLastConnectedNodeReachable = false;
							clientController.removeUnreachableLastConnectedNode(serverConnection, lastConnectedNode.getJSONString());
						}
					} else {
						if (!isLastConnectedNodePresent) {
							user.join();
						} else {
							LOGGER.severe("ALL last connected nodes are unreachable");
							isLastConnectedNodePresent = false;
						}
					}
				} else {
					LOGGER.warning("User undefined");
				}
			} while (isLastConnectedNodePresent && !isLastConnectedNodeReachable);
			clientController.sendLastNodeToServer(serverConnection, user);

		} catch (IOException ioException) {
			LOGGER.warning("Unable to connectToUserDatabase to local server. Reason " + ioException.toString());
		}
	}


}
