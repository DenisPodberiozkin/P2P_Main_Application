package User.Database;

import java.sql.Connection;

public class DataBaseController implements IDataBaseController {

    public Connection connectToDatabase() {
        Database database = Database.getInstance();
        return database.connect("Userdata");
    }
}
