package User.Database;

import java.sql.Connection;

public class DataBaseController implements IDataBaseController {

    private static DataBaseController instance;
    private final Database database = new Database();

    public static DataBaseController getInstance() {
        if (instance == null) {
            instance = new DataBaseController();
        }
        return instance;
    }

    public Connection connectToDatabase() {
        return database.connect("Userdata");
    }
}
