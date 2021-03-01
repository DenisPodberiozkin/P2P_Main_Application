package User.CommunicationUnit.Server;

import User.ConnectionsData;
import org.junit.jupiter.api.Test;

class ServerControllerTest {

    @Test
    void startServer() {
        ServerController.getInstance().startServer(ConnectionsData.getUserServerPort());
    }
}