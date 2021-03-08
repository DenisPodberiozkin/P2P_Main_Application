package User.NodeManager;

import GUI.ControllerFactory;
import User.CommunicationUnit.Server.IServerController;
import User.CommunicationUnit.Server.ServerController;
import User.ConnectionsData;
import User.Encryption.EncryptionController;
import User.NodeManager.Lookup.FindLookupLogic;
import User.NodeManager.Lookup.LookupEngine;
import User.NodeManager.Lookup.LookupLogic;
import User.NodeManager.Lookup.TransferMessageLookupLogic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.URL;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Logger;

import static User.NodeManager.NodeUtil.isBigger;

public class User extends Node {
    private static final Logger LOGGER = Logger.getLogger(User.class.getName());
    private final static int MAX_SUCCESSOR_QUEUE_SIZE = 10;
    private final static int MAX_RETRY_ATTEMPTS = 2;
    private static User instance;
    private final SortedMap<String, Node> nodes = Collections.synchronizedSortedMap(new TreeMap<String, Node>(Comparator.naturalOrder()));
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private final Updater updater;
    private final Deque<Node> successorsQueue = new LinkedList<>();
    private final IServerController serverController = ServerController.getInstance();
    private final HashMap<String, Conversation> conversations = new HashMap<>();
    private Node predecessor;
    private Node successor;
    private PrivateKey privateKey;

    public User(KeyPair keyPair) {
        this(keyPair.getPublic());
        this.privateKey = keyPair.getPrivate();
    }

    public User(PublicKey publicKey) {
        super(publicKey, ConnectionsData.getUserServerPort());
        initialiseIP();
        User.instance = this;
        this.updater = new Updater(this);
        ControllerFactory.getTestController().setNodeNameLabelText(getId());
        ControllerFactory.getTestController().setPortLabelText(getPort());
    }

    public User() { // TODO delete later
        super(EncryptionController.getInstance().generateKeyPair().getPublic(), ConnectionsData.getUserServerPort());
        initialiseIP();
        User.instance = this;
        this.updater = new Updater(this);
        ControllerFactory.getTestController().setNodeNameLabelText(getId());
        ControllerFactory.getTestController().setPortLabelText(getPort());

    }

    public static User getInstance() {
        return instance;
    }

    private void initialiseIP() {
        try (final DatagramSocket socket = new DatagramSocket()) {
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            String ip = socket.getLocalAddress().getHostAddress();
            String publicIp = initialisePublicIp();
            setIp(ip);
            setPublicIp(publicIp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String initialisePublicIp() throws IOException {
        URL whatIsMyIp = new URL("http://checkip.amazonaws.com");
        try (final BufferedReader in = new BufferedReader(new InputStreamReader(whatIsMyIp.openStream()))) {
            return in.readLine();
        }
    }

    public void join(Node successorNode) {
        serverController.startServer(ConnectionsData.getUserServerPort());
        readWriteLock.writeLock().lock();
        try {
            setSuccessorAndConnect(successorNode);
        } finally {
            readWriteLock.writeLock().unlock();
        }
        executorService.execute(updater);
        LOGGER.info("Joined to ring");
    }

    public void join() {
        serverController.startServer(ConnectionsData.getUserServerPort());
        executorService.execute(updater);
        LOGGER.info("Joined to ring");
    }

    public FutureTask<LinkedHashMap<String, Node>> checkConnections() {
        TableGenerator generator = new TableGenerator(this);
        return (FutureTask<LinkedHashMap<String, Node>>) executorService.submit(generator);
    }

    public FutureTask<Boolean> stabilize() {
        NodeStabilizer stabilizer = new NodeStabilizer(this);
        return (FutureTask<Boolean>) executorService.submit(stabilizer);
    }

    public void executeChanges(Node x, Node successor) {
        readWriteLock.writeLock().lock();
        try {
            setSuccessorAndConnect(x);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    public void updateFingerTable(LinkedHashMap<String, Node> updatedNodes) {
        readWriteLock.writeLock().lock();
        Node successor = getSuccessor();
        try {

            Node updatedSuccessor = updatedNodes.entrySet().iterator().next().getValue();
            if (!successor.equals(updatedSuccessor)) {
                setSuccessorAndConnect(updatedSuccessor);
                updatedNodes.remove(updatedSuccessor.getId());
            }

            SortedMap<String, Node> oldNodes = getNodes();

            LinkedList<Node> toRemove = new LinkedList<>();
            LinkedList<Node> toAdd = new LinkedList<>();

            for (String key : updatedNodes.keySet()) {
                Node candidate = updatedNodes.get(key);
                if (!oldNodes.containsKey(key)) {
                    toAdd.add(candidate);
                }
            }
            for (String key : oldNodes.keySet()) {
                Node oldNode = oldNodes.get(key);
                if (!updatedNodes.containsKey(key)) {
                    toRemove.add(oldNode);
                }
            }

            for (Node nodeToRemove : toRemove) {
                removeNodeFromTableAndDisconnect(nodeToRemove);
            }

            for (Node nodeToAdd : toAdd) {
                addNodeToTableAndConnect(nodeToAdd);
            }

        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    public boolean checkAndUpdateNewPredecessor(Node candidateNode) {
        readWriteLock.writeLock().lock();
        try {
            LOGGER.config("Starting checking " + candidateNode.getId() + " to make it new predecessor");
            boolean result = false;
            Node predecessor = getPredecessor();
            if (predecessor == null ||
                    (isBigger(candidateNode, predecessor)) && isBigger(this, candidateNode) ||
                    (isBigger(predecessor, this) && (isBigger(this, candidateNode) || isBigger(candidateNode, predecessor)))) {
                setPredecessorAndConnect(candidateNode);
                result = true;
            }

            if (!hasNeighbours()) {
                setSuccessorAndConnect(candidateNode);
            }

            return result;
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    @Override
    public String findNode(String id) {
        Node successor = getSuccessor();
        LookupLogic lookupLogic = new FindLookupLogic(successor, id);
        return lookup(id, successor, lookupLogic, "find");
    }

    public PrivateKey getPrivateKey() {
        readWriteLock.readLock().lock();
        try {
            return privateKey;
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    public void setPrivateKey(PrivateKey privateKey) {
        this.privateKey = privateKey;
    }

    @Override
    public Node getPredecessor() {
        readWriteLock.readLock().lock();
        try {
            return predecessor;
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    private void setPredecessor(Node predecessor) {
        readWriteLock.writeLock().lock();
        try {
            Node currentPredecessor = getPredecessor();
            if (currentPredecessor != null && currentPredecessor.isConnected()) {
                this.predecessor.closeConnection();
            }
            this.predecessor = predecessor;

            if (this.predecessor != null) {
                ControllerFactory.getTestController().setPredecessorLabelText(this.predecessor.getId());
                LOGGER.info("The new predecessor of node " + getId() + " is node " + predecessor.getId());
            } else {
                ControllerFactory.getTestController().setPredecessorLabelText("");
                LOGGER.info("Predecessor is removed");
            }

        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    @Override
    public boolean hasNeighbours() {
        return !getNodes().isEmpty();
    }

    public void setPredecessorAndConnect(Node predecessor) {
        readWriteLock.writeLock().lock();
        try {
            Node currentSuccessor = getSuccessor();
            if (predecessor != null && !predecessor.isConnected()) {
                if (predecessor.equals(currentSuccessor)) {
                    predecessor = predecessor.clone();
                }
                predecessor.connectToNode(false);
            }
            setPredecessor(predecessor);

        } catch (IOException ioException) {
            LOGGER.warning("Unable to connect to predecessor node");
        } finally {
            readWriteLock.writeLock().unlock();
        }

    }

    public Node getSuccessor() {
        readWriteLock.readLock().lock();
        try {
            return successor;
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    public void setSuccessorAndConnect(Node newSuccessor) {
        readWriteLock.writeLock().lock();
        try {
            Node currentSuccessor = getSuccessor();
            Node currentPredecessor = getPredecessor();
            if (currentSuccessor != null) {
                removeNodeFromTableAndDisconnect(currentSuccessor);
            }

            if (newSuccessor != null) {
                if (newSuccessor.equals(currentPredecessor)) {
                    newSuccessor = newSuccessor.clone();
                }
                replaceSuccessorsQueueHead(newSuccessor);

                final Node nodeFromTable = getNodeFromTable(newSuccessor.getId());
                if (nodeFromTable != null) {
                    nodeFromTable.notifyAboutNewPredecessor(this);
                    this.successor = nodeFromTable;
                } else {
                    addNodeToTableAndConnect(newSuccessor);
                    newSuccessor.notifyAboutNewPredecessor(this);
                    this.successor = newSuccessor;
                }


                LOGGER.info("The new successor of node " + getId() + " is node " + newSuccessor.getId());
                ControllerFactory.getTestController().setSuccessorLabelText(newSuccessor.getId());
            } else {
                setNewSuccessorFromSuccessorsQueue();
            }
        } finally {
            readWriteLock.writeLock().unlock();
        }

    }

    public SortedMap<String, Node> getNodes() {
        readWriteLock.readLock().lock();
        try {
            return nodes;
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    public Node getNodeFromTable(String id) {
        readWriteLock.readLock().lock();
        try {
            return nodes.get(id);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    public void addNodeToTableAndConnect(Node node) {
        readWriteLock.writeLock().lock();
        try {
            ControllerFactory.getTestController().addNodeToFingerTable(node);
            if (!node.isConnected()) {
                node.connectToNode(false);
            }
            nodes.put(node.getId(), node);
        } catch (IOException ioException) {
            LOGGER.warning("Unable to connect to node " + node.getIp() + ":" + node.getPort() + ". Reason " + ioException.toString());
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    public void removeNodeFromTableAndDisconnect(Node node) {
        readWriteLock.writeLock().lock();
        try {
            ControllerFactory.getTestController().removeNodeFromFIngerTable(node);
            nodes.remove(node.getId());
            if (node.isConnected()) {
                node.closeConnection();
            }
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    public void replaceSuccessorsQueueHead(Node node) {
        readWriteLock.writeLock().lock();
        try {
            successorsQueue.pollFirst();
            successorsQueue.offerFirst(node);
            ControllerFactory.getTestController().updateSuccessorsData(this.successorsQueue);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    private void setNewSuccessorFromSuccessorsQueue() {
        readWriteLock.writeLock().lock();
        try {

            successorsQueue.pollFirst();
            Node newSuccessor = successorsQueue.peekFirst();
            if (newSuccessor != null) {
                setSuccessorAndConnect(newSuccessor);
            } else {
                this.successor = null;
                LOGGER.info("Current successor is removed");
                ControllerFactory.getTestController().setSuccessorLabelText("");
                ControllerFactory.getTestController().updateSuccessorsData(successorsQueue);
            }
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    @Override
    public Deque<Node> getSuccessorsQueue() {
        readWriteLock.readLock().lock();
        try {
            return successorsQueue;
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    public void updateSuccessorsList(Node successor) {
        Queue<Node> successorsQueue = getSuccessor().getSuccessorsQueue();
        Queue<Node> newSuccessorsQueue = new LinkedList<>();
        newSuccessorsQueue.offer(successor);

        if (successorsQueue != null) {
            int i = 0;
            boolean isReachedEnd = false;
            final int successorsQueueSize = successorsQueue.size();
            if (!successorsQueue.isEmpty()) {
                while (!isReachedEnd && i < successorsQueueSize && i < MAX_SUCCESSOR_QUEUE_SIZE) {
                    Node candidateNode = successorsQueue.remove();
                    if (!candidateNode.equals(this)) {
                        newSuccessorsQueue.offer(candidateNode);
                        LOGGER.config("Node " + candidateNode.getPort() + " was added to successors queue");
                    } else {
                        isReachedEnd = true;
                    }

                    i++;
                }
            }
        }


        readWriteLock.writeLock().lock();
        try {
            this.successorsQueue.clear();
            this.successorsQueue.addAll(newSuccessorsQueue);
        } finally {
            readWriteLock.writeLock().unlock();
        }

        ControllerFactory.getTestController().updateSuccessorsData(this.successorsQueue);

    }

    public void notifyDisconnection(Node disconnectedNode) {
        readWriteLock.writeLock().lock();
        try {
            Node currentSuccessor = getSuccessor();
            Node currentPredecessor = getPredecessor();
            LOGGER.info(disconnectedNode + "");
            if (disconnectedNode.equals(currentSuccessor)) {
                LOGGER.info(currentSuccessor.getId());
                setSuccessorAndConnect(null);
            }
            if (disconnectedNode.equals(currentPredecessor)) {
                LOGGER.info(currentPredecessor.getId());
                setPredecessorAndConnect(null);
            }

            if (!disconnectedNode.equals(currentPredecessor) && !disconnectedNode.equals(currentSuccessor)) {
                removeNodeFromTableAndDisconnect(disconnectedNode);
            }
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    public Conversation createNewConversation(String participantId) {
        Conversation conversation = new Conversation(participantId);
        conversations.put(participantId, conversation);
        return conversation;
    }

    public void processMessage(String payload) {
        //TODO decryption
        String[] tokens = payload.split(" ");
        String senderId = tokens[0];
        String text = tokens[1];
        addMessage(senderId, senderId, text);
        LOGGER.info("Received message " + text + " from " + senderId);
    }

    public void addMessage(String participantId, String senderId, String text) {
        Conversation conversation = conversations.get(senderId);
        if (conversation == null) {
            conversation = createNewConversation(participantId);
        }
        conversation.addMessage(senderId, text);
    }

    public void sendMessage(String receiverId, String text) {
        String userId = getId();
        addMessage(receiverId, userId, text);

        String payload = userId + " " + text; //TODO encryption

        transferMessage(receiverId, payload);
    }

    @Override
    public String transferMessage(String receiverId, String payload) {
        Node successor = getSuccessor();
        LookupLogic lookupLogic = new TransferMessageLookupLogic(receiverId, payload);
        return lookup(receiverId, successor, lookupLogic, "Transfer Message");
    }

    private String lookup(String lookupId, Node successor, LookupLogic lookupLogic, String taskName) {
        boolean isTryAgain;
        int counter = 0;
        String findResult = "NF";
        do {
            try {
                LookupEngine lookupEngine = new LookupEngine(this, lookupId, successor, lookupLogic);
                findResult = executorService.submit(lookupEngine).get();
                isTryAgain = findResult.equals("NF");
            } catch (InterruptedException | ExecutionException e) {
                LOGGER.warning("Unable to receive reply from " + taskName.toUpperCase() + " message session. Reason: " + e.toString());
                isTryAgain = true;
            }

            if (isTryAgain) {
                counter++;
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    LOGGER.warning("Unable to wait for retry. Reason " + e.toString());
                }
            }
        } while (isTryAgain && counter < MAX_RETRY_ATTEMPTS);


        return findResult;
    }
}
