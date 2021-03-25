package User.Database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
    private final static String JDBC = "jdbc:sqlite:";
    private final static String PATH = "src/main/resources/db/";
    private Connection connection;


    /**
     * Connect to the database or creates and initializes the new database
     *
     * @param dbName database name
     * @return Connection to the database
     */
    public Connection connect(String dbName) {
        String fullPath = PATH + dbName + ".db";
        String fullURL = JDBC + fullPath;
//        Connection connection = null;

        try {
            boolean isNew = false;
            if (!new File(fullPath).exists()) {
                isNew = true;
            }
            connection = DriverManager.getConnection(fullURL);
            if (isNew) {
                initCreatedDB(dbName, connection);
            }
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            return connection;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Connection getConnection() {
        return connection;
    }


    private void initCreatedDB(String dbName, Connection connection) throws SQLException {

        String sql = "create table " + dbName +
                "(   public_key  blob," +
                "    private_key blob," +
                "    id          text not null" +
                "        constraint " + dbName + "_pk" +
                "            primary key" +
                ");";

        Statement statement = connection.createStatement();
        statement.execute(sql);


    }
}
