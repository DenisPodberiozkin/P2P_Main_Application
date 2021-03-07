package User.CommunicationUnit.Client;

import GUI.ControllerFactory;
import User.CommunicationUnit.MessageReader;
import User.CommunicationUnit.Server.InboundTokens;
import User.CommunicationUnit.SynchronisedWriter;
import User.Encryption.Hash;
import User.NodeManager.Node;
import User.NodeManager.User;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.*;
import java.util.logging.Logger;

public class OutboundConnection implements AutoCloseable {
    private static final Logger LOGGER = Logger.getLogger(OutboundConnection.class.getName());
    private final static int MAX_THREAD = Hash.getHashSize();
    private final String ip;
    private final int port;
    private final HashMap<Integer, OutboundSession> sessions;
    private final ExecutorService executor;
    private Socket socket;
    private MessageReader reader;
    private SynchronisedWriter writer;
    private ClientReceiver receiver;
    private HeartBeatSender heartBeatSender;
    private Node assignedNode;

    public OutboundConnection(String ip, int port) {
        this.ip = ip;
        this.port = port;
        this.sessions = new HashMap<>();
        this.executor = Executors.newFixedThreadPool(MAX_THREAD);
    }

    public OutboundConnection(String ip, int port, Node assignedNode) {
        this(ip, port);
        this.assignedNode = assignedNode;
    }

    @Override
    public void close() throws IOException {
        closeConnection();
    }

    public void createConnection() throws IOException {
        if (ip.equals(User.getInstance().getIp()) && port == User.getInstance().getPort()) {
            LOGGER.warning("Trying to connect to yourself");
        }
        socket = new Socket(ip, port);
        reader = new MessageReader(new InputStreamReader(socket.getInputStream()));
        writer = new SynchronisedWriter(socket.getOutputStream(), true);
        receiver = new ClientReceiver(reader, this);
        heartBeatSender = new HeartBeatSender(this);
        Thread receiverThread = new Thread(receiver);
        receiverThread.setName("Outbound Receiver " + toString());
        receiverThread.start();
        Thread heartBeatSenderThread = new Thread(heartBeatSender);
        heartBeatSenderThread.setName("Outbound Heart Beat Sender");
        heartBeatSenderThread.start();

        ControllerFactory.getTestController().addOutboundConnection(this);
        LOGGER.info("Established connection to " + ip + ":" + port);


    }

    public FutureTask<String> sendMessage(String message) throws RejectedExecutionException {
        OutboundSession session = new OutboundSession(writer, message, this);

        sessions.put(session.getId(), session);
        try {
            return (FutureTask<String>) executor.submit(session);
        } catch (RejectedExecutionException e) {
            closeSession(session.getId());
            throw new RejectedExecutionException("Connection is closed!");
        }
    }

    public void closeConnection() throws IOException {
        LOGGER.config("START Disconnected from " + ip + ":" + port);
//        if (assignedNode != null) {
//            this.assignedNode.removeConnection();
//            User.getInstance().removeFromTable(assignedNode);
//        }

        this.assignedNode = null;
        if (heartBeatSender != null) {
            heartBeatSender.stopHeartBeat();
        }

        executor.shutdown();
        try {
            if (!executor.awaitTermination(5000, TimeUnit.MILLISECONDS)) {
                executor.shutdownNow();
            }

        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
        OutboundConnectionManager.getInstance().closeConnection(this);

        if (receiver != null) {
            receiver.stopReceiver();
        }

        if (socket != null) {
            socket.close();
        }

        if (reader != null) {
            reader.close();
        }
        if (writer != null) {
            writer.close();
        }

        LOGGER.info("Disconnected from " + ip + ":" + port);
    }

    public void closeSession(int id) {
        if (sessions.get(id).getMessage().contains(InboundTokens.PING.getToken())) {
            LOGGER.config("Outbound Session " + id + " is removed");
        } else {
            LOGGER.info("Outbound Session " + id + " is removed");
        }

        sessions.remove(id);
    }

    public OutboundSession getSession(int id) {
        return sessions.get(id);
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public Node getAssignedNode() {
        return assignedNode;
    }

//    public void setAssignedNode(Node assignedNode) {
//        this.assignedNode = assignedNode;
//    }
}
