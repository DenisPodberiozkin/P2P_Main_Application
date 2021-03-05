package User.CommunicationUnit.Client;

import User.NodeManager.Node;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RejectedExecutionException;

public interface IClientController {

    OutboundConnection connect(String ip, int port) throws IOException;

//    FutureTask<String> sendMessage(OutboundConnection connection, String message);

    OutboundConnection connect(String ip, int port, Node assignedNode) throws IOException;

    Node getLastConnectedNode(OutboundConnection connection);

    boolean hasNeighbours(OutboundConnection connection);

    FutureTask<String> lookUp(OutboundConnection connection, String id);

    void sendLastNodeToServer(OutboundConnection connection, Node node);

    void sendNotificationAboutNewPredecessor(OutboundConnection connection, Node node);

    Node getPredecessor(OutboundConnection connection) throws RejectedExecutionException;

    Queue<Node> getSuccessorsQueue(OutboundConnection connection);
}