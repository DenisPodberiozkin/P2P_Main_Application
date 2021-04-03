package User.CommunicationUnit.Server;

import javafx.beans.property.SimpleIntegerProperty;

public interface IServerController {

    void startServer(int port);

    SimpleIntegerProperty getServerPortProperty();
}
