package User.CommunicationUnit.Client;

import User.NodeManager.Node;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.util.Deque;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RejectedExecutionException;

public interface IClientController {

    OutboundConnection connect(String ip, int port) throws IOException;

//    FutureTask<String> sendMessage(OutboundConnection connection, String message);

    OutboundConnection connect(String ip, int port, Node assignedNode) throws IOException;

    Node getLastConnectedNode(OutboundConnection connection);

    boolean hasNeighbours(OutboundConnection connection);

    String lookUp(OutboundConnection connection, String id);

    void sendLastNodeToServer(OutboundConnection connection, Node node);

    void sendNotificationAboutNewPredecessor(OutboundConnection connection, Node node);

    Node getPredecessor(OutboundConnection connection) throws RejectedExecutionException;

    Deque<Node> getSuccessorsQueue(OutboundConnection connection);

    void removeUnreachableLastConnectedNode(OutboundConnection connection, String nodeJSON);

    String transferMessage(OutboundConnection connection, String receiverId, String payload);

    PublicKey exchangePublicKeys(OutboundConnection connection, PublicKey publicKeyToSend) throws ExecutionException, InterruptedException, IllegalArgumentException, GeneralSecurityException;

    String transferPublicKey(OutboundConnection connection, String publicKeyToSend64, String receiverId, long messageSessionId);
}