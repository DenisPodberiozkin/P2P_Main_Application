package User.Database.DAO;

import Encryption.EncryptionController;
import Encryption.IEncryptionController;
import User.NodeManager.User;

import java.security.PublicKey;
import java.sql.*;
import java.util.logging.Logger;

public class UserDAO {
	private static final Logger LOGGER = Logger.getLogger(UserDAO.class.getName());
	private final Connection connection;
	private final IEncryptionController encryptionController;

	public UserDAO(Connection connection) {
		this.connection = connection;
		this.encryptionController = EncryptionController.getInstance();
	}


	public void addUser(String id, byte[] publicKey, byte[] encryptedPrivateKey) {
		final String addUser = "INSERT INTO userdata (id, public_key, private_key)" +
				"VALUES (?, ?, ?);";

		try {
			connection.setAutoCommit(false);
			try (PreparedStatement statement = connection.prepareStatement(addUser)) {
				statement.setString(1, id);
				statement.setBytes(2, publicKey);
				statement.setBytes(3, encryptedPrivateKey);
				statement.executeUpdate();
				connection.commit();

			} catch (SQLException throwables) {
				connection.rollback();
				throwables.printStackTrace();
			} finally {
				connection.setAutoCommit(true);
			}
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}


	}

	public User retrieveUserPublicData() {
		final String getPublicData = "SELECT public_key FROM userdata";

		try {
			connection.setAutoCommit(false);
			try (Statement statement = connection.createStatement()) {
				ResultSet resultSet = statement.executeQuery(getPublicData);
				byte[] publicKeyData = resultSet.getBytes("public_key");
				PublicKey publicKey = encryptionController.getPublicKeyFromBytes(publicKeyData);
				connection.commit();
				return new User(publicKey);
			} catch (SQLException throwables) {
				connection.rollback();
				LOGGER.warning("Error retrieving user public data. Reason" + throwables.getMessage());
				throwables.printStackTrace();
			} finally {
				connection.setAutoCommit(false);
			}
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}


		return null;
	}


	public byte[] retrieveEncryptedPrivateKey() {
		final String getPrivateKey = "SELECT private_key FROM userdata";

		try {
			connection.setAutoCommit(false);
			try (Statement statement = connection.createStatement()) {
				ResultSet resultSet = statement.executeQuery(getPrivateKey);
				connection.commit();
				return resultSet.getBytes("private_key");
			} catch (SQLException throwables) {
				connection.rollback();
				LOGGER.warning("Error retrieving user encrypted private key. Reason " + throwables.getMessage());
				throwables.printStackTrace();
			}
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}

		return null;
	}
}
