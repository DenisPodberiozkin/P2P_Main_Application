package User.CommunicationUnit.Client;

public interface IClientController {

    OutboundConnection connect(String ip, int port, boolean isSingle);

    void sendMessage(OutboundConnection connection, String message);

    void closeConnection(OutboundConnection connection);

    String getLastResponse(OutboundConnection connection);
}
