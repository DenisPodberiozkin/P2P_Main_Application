package User.Database;

import java.sql.Connection;

public class DataBaseController implements IDataBaseController {

    private static DataBaseController instance;

    public static DataBaseController getInstance() {
        if (instance == null) {
            instance = new DataBaseController();
        }
        return instance;
    }

    public Connection connectToDatabase() {
        Database database = Database.getInstance();
        return database.connect("Userdata");
    }
}
