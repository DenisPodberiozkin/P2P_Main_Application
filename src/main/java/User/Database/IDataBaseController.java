package User.Database;

import User.Database.DatabaseExceptions.DatabaseInitException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public interface IDataBaseController {

    Connection connectToUserDatabase(String userDirectoryName, UserDataBase userDataBase) throws FileNotFoundException, SQLException;

    Connection connectToSettingsDatabase() throws FileNotFoundException, SQLException;

    UserDataBase createUserDatabase(String userDirectoryName) throws IOException;

    void initUserDataBase(UserDataBase userDataBase, Connection connection) throws DatabaseInitException;
}
