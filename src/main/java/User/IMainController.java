package User;

import User.Database.DatabaseExceptions.DatabaseInitException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.SQLException;

public interface IMainController {
    void initSettings() throws FileNotFoundException, SQLException;

    void createAccount(String password, String secretPassword, String username) throws IOException, SQLException, DatabaseInitException;

    void loginToAccount(String password, String secretPassword, String username) throws FileNotFoundException, SQLException, GeneralSecurityException;

    void connectToRing();

}

