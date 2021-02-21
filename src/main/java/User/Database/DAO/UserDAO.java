package User.Database.DAO;

import User.Encryption.EncryptionController;
import User.Encryption.IEncryptionController;
import User.NodeManager.User;

import java.security.PublicKey;
import java.sql.*;

public class UserDAO {
    private final Connection connection;
    private final IEncryptionController encryptionController;

    public UserDAO(Connection connection) {
        this.connection = connection;
        this.encryptionController = EncryptionController.getInstance();
    }


    public void addUser(String id, byte[] publicKey, byte[] encryptedPrivateKey) {
        final String addUser = "INSERT INTO userdata (id, public_key, private_key)" +
                "VALUES (?, ?, ?);";

        try (PreparedStatement statement = connection.prepareStatement(addUser)) {
            connection.setAutoCommit(false);
            statement.setString(1, id);
            statement.setBytes(2, publicKey);
            statement.setBytes(3, encryptedPrivateKey);
            statement.executeUpdate();
            connection.commit();

        } catch (SQLException throwables) {
            try {
                connection.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            throwables.printStackTrace();
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }


    }

    public User retrieveUserPublicData() {
        final String getPublicData = "SELECT public_key FROM userdata";
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(getPublicData);
//            resultSet.first();
            byte[] publicKeyData = resultSet.getBytes("public_key");
            PublicKey publicKey = encryptionController.getPublicKeyFromBytes(publicKeyData);
            return new User(publicKey);
        } catch (SQLException throwables) {
            System.err.println("Error retrieving user public data");
            throwables.printStackTrace();
        }
        return null;
    }


    public byte[] retrieveEncryptedPrivateKey() {
        final String getPrivateKey = "SELECT private_key FROM userdata";

        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(getPrivateKey);
//            resultSet.first();
            return resultSet.getBytes("private_key");
        } catch (SQLException throwables) {
            System.err.println("Error retrieving user encrypted private key");
            throwables.printStackTrace();
        }
        return null;
    }
}
