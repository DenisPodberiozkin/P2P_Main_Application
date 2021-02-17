package User.Database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
    private static Database instance;
    private final String jdbc = "jdbc:sqlite:";
    private final String path = "src/main/resources/db/";
    private Connection connection;

    public static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }


    /**
     * Connect to the database or creates and initializes the new database
     *
     * @param dbName database name
     * @return Connection to the database
     */
    public Connection connect(String dbName) {
        String fullPath = path + dbName + ".db";
        String fullURL = jdbc + fullPath;
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
