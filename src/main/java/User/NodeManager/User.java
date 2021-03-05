package User.NodeManager;

import GUI.ControllerFactory;
import User.ConnectionsData;
import User.Encryption.EncryptionController;

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
    private static User instance;
    private final SortedMap<String, Node> nodes = Collections.synchronizedSortedMap(new TreeMap<String, Node>(Comparator.naturalOrder()));
    private Node predecessor;
    private Node successor;
    private PrivateKey privateKey;
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private final Updater updater;
    private final Queue<Node> successorsQueue = new LinkedList<>();

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
        readWriteLock.writeLock().lock();
        try {
            addNodeToTableAndConnect(successorNode);
            setSuccessor(successorNode);
        } finally {
            readWriteLock.writeLock().unlock();
        }
        successor.notifyAboutNewPredecessor(this);
        //TODO check connections
        join();
    }

    public void join() {
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
            removeNodeFromTableAndDisconnect(successor);
            addNodeToTableAndConnect(x);
            setSuccessor(x);
        } finally {
            readWriteLock.writeLock().unlock();
        }
        x.notifyAboutNewPredecessor(this);
    }

    public void updateFingerTable(LinkedHashMap<String, Node> updatedNodes) {
        readWriteLock.writeLock().lock();
        SortedMap<String, Node> oldNodes = getNodes();
        Node successor = getSuccessor();
        try {

            Node updatedSuccessor = updatedNodes.entrySet().iterator().next().getValue();
            if (!successor.equals(updatedSuccessor)) {
                setSuccessor(updatedSuccessor);
            }

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
                Node candidateNodeClone = candidateNode.clone();
                addNodeToTableAndConnect(candidateNodeClone);
                setSuccessor(candidateNodeClone);
                candidateNodeClone.notifyAboutNewPredecessor(this);
            }

            return result;
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    public FutureTask<String> lookUp(String id) {
        Lookup lookup = new Lookup(this, id);
        return (FutureTask<String>) executorService.submit(lookup);
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

    @Override
    public boolean hasNeighbours() {
        return !getNodes().isEmpty();
    }

    public void setPredecessorAndConnect(Node predecessor) {
        readWriteLock.writeLock().lock();
        try {
            if (this.predecessor != null) {
                this.predecessor.closeConnection();
            }
            this.predecessor = predecessor;
            ControllerFactory.getTestController().setPredecessorLabelText(this.predecessor.getId());
            if (!predecessor.isConnected()) {
//            LOGGER.severe("new connection to the same node as predecessor");//TODO make copy if it the same node
                predecessor.connectToNode(false);
            }
            LOGGER.info("The new predecessor of node " + getId() + " is node " + predecessor.getId());
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

    public void setSuccessor(Node successor) {
        readWriteLock.writeLock().lock();
        try {
            this.successor = successor;
            ControllerFactory.getTestController().setSuccessorLabelText(this.successor.getId());
            addToSuccessorQueue(successor);
            LOGGER.info("The new successor of node " + getId() + " is node " + successor.getId());
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
            nodes.put(node.getId(), node);
            ControllerFactory.getTestController().addNodeToFingerTable(node);
            if (!node.isConnected()) {
                node.connectToNode(false);
            }
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    public void removeNodeFromTableAndDisconnect(Node node) {
        readWriteLock.writeLock().lock();
        try {
            ControllerFactory.getTestController().removeNodeFromFIngerTable(node);
            nodes.remove(node.getId());
            node.closeConnection();
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    public void addToSuccessorQueue(Node node) {
        readWriteLock.writeLock().lock();
        try {
            successorsQueue.offer(node);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    public void addToSuccessorList(Node node) {
        readWriteLock.writeLock().lock();
        try {
            successorsQueue.add(node);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    public void removeFromSuccessorList(Node node) {
        readWriteLock.writeLock().lock();
        try {
            successorsQueue.remove(node);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    @Override
    public Queue<Node> getSuccessorsQueue() {
        readWriteLock.readLock().lock();
        try {
            return successorsQueue;
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    public void updateSuccessorsList(Node successor) {
        Queue<Node> successorsQueue = successor.getSuccessorsQueue();
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
}
