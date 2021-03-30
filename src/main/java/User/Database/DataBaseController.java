package User.Database;

import User.Database.DatabaseExceptions.DatabaseInitException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class DataBaseController implements IDataBaseController {

    private static DataBaseController instance;

    public static DataBaseController getInstance() {
        if (instance == null) {
            instance = new DataBaseController();
        }
        return instance;
    }

    @Override
    public Connection connectToUserDatabase(String userDirectoryName, UserDataBase userDataBase) throws FileNotFoundException, SQLException {
        return userDataBase.connectToUserDatabase("Userdata", userDirectoryName);
    }

    @Override
    public Connection connectToSettingsDatabase() throws FileNotFoundException, SQLException {
        final SettingsDataBase settingsDataBase = new SettingsDataBase();
        return settingsDataBase.connectToSettingsDatabase("Settings");
    }

    @Override
    public UserDataBase createUserDatabase(String userDirectoryName) throws IOException {
        UserDataBase userDataBase = new UserDataBase();
        userDataBase.createDb("Userdata", userDirectoryName);
        return userDataBase;
    }

    @Override
    public void initUserDataBase(UserDataBase userDataBase, Connection connection) throws DatabaseInitException {
        try {
            userDataBase.initDb("Userdata", connection);
        } catch (SQLException throwables) {
            throw new DatabaseInitException("Unable to initialise user database");
        }
    }
}
