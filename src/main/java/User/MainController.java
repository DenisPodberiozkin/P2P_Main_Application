package User;

import Encryption.EncryptionController;
import Encryption.IEncryptionController;
import User.CommunicationUnit.Client.ClientController;
import User.CommunicationUnit.Client.IClientController;
import User.CommunicationUnit.Server.IServerController;
import User.CommunicationUnit.Server.ServerController;
import User.Database.DAO.UserDAO;
import User.Database.DataBaseController;
import User.Database.DatabaseExceptions.DatabaseInitException;
import User.Database.IDataBaseController;
import User.Database.UserDataBase;
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
		user.setUsername(username);
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
	public void loginToAccount(String password, String secretPassword, String username) throws IOException, SQLException, GeneralSecurityException {
		try {
			serverController.startServer(ApplicationSettingsModel.getApplicationPort());
			final Connection connection = dataBaseController.connectToUserDatabase(username, new UserDataBase());
			final UserDAO userDAO = new UserDAO(connection);
			final User user = userDAO.retrieveUserPublicData();
			user.setPort(serverController.getServer().getPort());
			user.setUsername(username);
			final byte[] encryptedPrivateKeyData = userDAO.retrieveEncryptedPrivateKey();

			final SecretKey secretKey = encryptionController.generateAESKey(password, secretPassword);
			user.setSecretKey(secretKey);
			final byte[] decryptedPrivateKeyData = encryptionController.decryptDataByAES(secretKey, encryptedPrivateKeyData);

			final PrivateKey privateKey = encryptionController.getPrivateKeyFromBytes(decryptedPrivateKeyData);
			user.setPrivateKey(privateKey);

			user.initConversations(connection);

			connectToRing(user);

		} catch (SQLException throwables) {
			serverController.stopServer();
			throw new SQLException(throwables.getMessage());
		} catch (GeneralSecurityException e) {
			serverController.stopServer();
			throw new GeneralSecurityException(e.getMessage());
		} catch (IOException ioException) {
			serverController.stopServer();
			throw new IOException(ioException.getMessage());
		}

	}

	private void connectToRing(User user) throws IOException {
		final ConnectionLogic connectionLogic = ConnectionFactory.getConnectionLogic(ConnectionSettingsModel.getConnectionType());
		connectionLogic.connect();

	}


}
