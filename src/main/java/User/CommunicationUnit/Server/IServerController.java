package User.CommunicationUnit.Server;

public interface IServerController {

	void startServer(int port);

	void stopServer();

	Server getServer();
}
