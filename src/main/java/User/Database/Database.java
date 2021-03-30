package User.Database;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class Database {
    private final static String JDBC = "jdbc:sqlite:";
    protected final String path;
    protected Connection connection;

    public Database(String path) {
        this.path = path;
    }

    public Connection getConnection() {
        return connection;
    }

    protected void connect(String fullPath) throws SQLException, FileNotFoundException {
        final String fullURL = JDBC + fullPath;
        if (new File(fullPath).exists()) {
            connection = DriverManager.getConnection(fullURL);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        } else {
            throw new FileNotFoundException("File " + fullPath + " not found");
        }
    }

}
