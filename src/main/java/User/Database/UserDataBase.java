package User.Database;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class UserDataBase extends Database {

    public UserDataBase() {
        super("src/main/resources/db/");
    }

    /**
     * Connect to the database or creates and initializes the new database
     *
     * @param dbName database name
     * @return Connection to the database
     */

    public Connection connectToUserDatabase(String dbName, String username) throws SQLException, FileNotFoundException {
        String fullPath = path + username + "/" + dbName + ".db";
        super.connect(fullPath);
        return connection;

    }

    public void createDb(String dbName, String username) throws IOException {
        final File file = new File(path + username + "/" + dbName + ".db");
        file.getParentFile().mkdir();
        file.createNewFile();
    }

    public void initDb(String dbName, Connection connection) throws SQLException {

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

    private void createUserDirectory(String username) {
        File file = new File(path + username);
        if (!file.exists()) {
            file.mkdir();
        }
    }
}
