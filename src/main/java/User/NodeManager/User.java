package User.NodeManager;

import GUI.ControllerFactory;
import User.Database.DAO.ConversationDAO;
import User.NodeManager.Exceptions.ConversationException;
import User.NodeManager.Exceptions.InboundMessageSessionNotFound;
import User.NodeManager.Exceptions.MessageException;
import User.NodeManager.Exceptions.SecureMessageChannelException;
import User.NodeManager.Lookup.*;
import User.NodeManager.MessageSession.InboundMessageSession;
import User.NodeManager.MessageSession.OutboundMessageSession;
import User.NodeManager.MessageSession.OutboundMessageSessionBuilder;
import User.Settings.ApplicationSettingsModel;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.sql.Connection;
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
    private final ObservableList<Conversation> conversations = FXCollections.observableArrayList();
    private final HashMap<Long, InboundMessageSession> inboundMessageSessions = new HashMap<>();
    private ConversationDAO conversationDAO;
    private String username;
    private Node predecessor;
    private Node successor;
    private PrivateKey privateKey;
    private SecretKey secretKey;

    public User(KeyPair keyPair) {
        this(keyPair.getPublic());
        this.privateKey = keyPair.getPrivate();
    }

    public User(PublicKey publicKey) {
        super(publicKey);

        setIp(ApplicationSettingsModel.getApplicationIp());
        User.instance = this;
        this.updater = new Updater(this);
        ControllerFactory.getDebuggerController().setNodeNameLabelText(getId());
        ControllerFactory.getDebuggerController().setPortLabelText(getPort());
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public static User getInstance() {
        return instance;
    }


    public void join(Node successorNode) {
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
                ControllerFactory.getDebuggerController().setPredecessorLabelText(this.predecessor.getId());
                LOGGER.info("The new predecessor of node " + getId() + " is node " + predecessor.getId());
            } else {
                ControllerFactory.getDebuggerController().setPredecessorLabelText("");
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
                predecessor.connectToNode();
            }
            setPredecessor(predecessor);

        } catch (IOException ioException) {
            LOGGER.warning("Unable to connectToUserDatabase to predecessor node");
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
                ControllerFactory.getDebuggerController().setSuccessorLabelText(newSuccessor.getId());
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
            ControllerFactory.getDebuggerController().addNodeToFingerTable(node);
            if (!node.isConnected()) {
                node.connectToNode();
            }
            nodes.put(node.getId(), node);
        } catch (IOException ioException) {
            LOGGER.warning("Unable to connectToUserDatabase to node " + node.getIp() + ":" + node.getPort() + ". Reason " + ioException.toString());
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    public void removeNodeFromTableAndDisconnect(Node node) {
        readWriteLock.writeLock().lock();
        try {
            ControllerFactory.getDebuggerController().removeNodeFromFIngerTable(node);
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
            ControllerFactory.getDebuggerController().updateSuccessorsData(this.successorsQueue);
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
                ControllerFactory.getDebuggerController().setSuccessorLabelText("");
                ControllerFactory.getDebuggerController().updateSuccessorsData(successorsQueue);
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

        ControllerFactory.getDebuggerController().updateSuccessorsData(this.successorsQueue);

    }

    public void notifyDisconnection(Node disconnectedNode) {
        if (disconnectedNode != null) {
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
    }

    public Conversation createNewConversation(String participantId) throws ConversationException {
        readWriteLock.writeLock().lock();
        try {
            final Conversation conversation = new Conversation(participantId);
            if (conversations.contains(conversation)) {
                throw new ConversationException("Conversation with " + participantId + " is already exists");
            }
            conversation.setConversationDAO(conversationDAO);
            Platform.runLater(() -> conversations.add(conversation));
            conversationDAO.addConversation(conversation);
            return conversation;
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    public void createNewConversation(String participantId, String conversationName) throws ConversationException {
        readWriteLock.writeLock().lock();
        try {
            Conversation conversation = createNewConversation(participantId);
            conversation.setConversationName(conversationName);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    public void removeConversation(Conversation conversation) {
        readWriteLock.writeLock().lock();
        try {
            conversationDAO.removeConversation(conversation);
            conversation.removeAllMessages();
            conversations.remove(conversation);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    public ObservableList<Conversation> getConversations() {
        return conversations;
    }

    public void processMessage(String payload) throws InboundMessageSessionNotFound {
        final String[] tokens = payload.split(" ");
        final long inboundMessageSessionId = Long.parseLong(tokens[0]);
        final InboundMessageSession inboundMessageSession = findInboundMessageSessionById(inboundMessageSessionId);
        final String encryptedPayload = tokens[1];

        inboundMessageSession.messageNotify(encryptedPayload);
        removeInboundMessageSession(inboundMessageSessionId);

    }

    public void addMessage(String participantId, String senderId, String text, boolean isSentByUser) {
        try {
            Conversation conversation = getConversation(participantId);
            if (conversation == null) {
                conversation = createNewConversation(participantId);
            }
            readWriteLock.writeLock().lock();
            try {
                conversation.addMessage(text, senderId, isSentByUser);
            } finally {
                readWriteLock.writeLock().unlock();
            }
        } catch (ConversationException e) {
            LOGGER.warning("Attempt to create conversation duplicate");
        }
    }

    private Conversation getConversation(String senderId) {
        readWriteLock.readLock().lock();
        try {
            for (Conversation conversation : conversations) {
                if (conversation.getParticipantId().equals(senderId)) {
                    return conversation;
                }
            }
            return null;
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    public void sendMessage(String receiverId, String text) throws MessageException, SecureMessageChannelException {
        try {
            String userId = getId();
            final String reply = createOutboundMessageSession(receiverId, text).get();
            if (reply.equals("OK")) {
                addMessage(receiverId, userId, text, true);
                LOGGER.info("Message " + text + " to " + receiverId + " was delivered successfully");
            } else if (reply.equals("NF")) {
                LOGGER.warning("Message undelivered. Reason - recipient not found.");
                throw new MessageException("Recipient not found/offline");
            } else if (reply.contains("Error")) {
                LOGGER.warning("Message undelivered due to Error - " + reply);
                throw new MessageException("Error occurred - " + reply);
            }
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.warning("Unable to get reply from outbound message session due to " + e.toString());
            if (e.getCause() instanceof SecureMessageChannelException) {
                throw new SecureMessageChannelException(e.getCause().getMessage());
            }
        }
    }

    private FutureTask<String> createOutboundMessageSession(String receiverId, String text) {
        OutboundMessageSession outboundMessageSession = new OutboundMessageSessionBuilder().setMessageText(text)
                .setParticipantNodeId(receiverId)
                .createOutboundMessageSession();

        return (FutureTask<String>) executorService.submit(outboundMessageSession);
    }

    public InboundMessageSession createInboundMessageSession(long participantMessageSessionId, PublicKey receivedPublicKey) throws GeneralSecurityException {
        InboundMessageSession inboundMessageSession = new InboundMessageSession(participantMessageSessionId, receivedPublicKey);
        readWriteLock.writeLock().lock();
        try {
            inboundMessageSessions.put(inboundMessageSession.getId(), inboundMessageSession);
        } finally {
            readWriteLock.writeLock().unlock();
        }
        executorService.submit(inboundMessageSession);
        return inboundMessageSession;
    }

    private InboundMessageSession findInboundMessageSessionById(long id) throws InboundMessageSessionNotFound {
        readWriteLock.writeLock().lock();
        try {
            if (inboundMessageSessions.containsKey(id)) {
                return inboundMessageSessions.get(id);
            } else {
                throw new InboundMessageSessionNotFound("Inbound Message Session with id " + id + " not found!");
            }
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    private void removeInboundMessageSession(long id) {
        readWriteLock.writeLock().lock();
        try {
            inboundMessageSessions.remove(id);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }


    @Override
    public String transferMessage(String receiverId, String payload) {
        Node successor = getSuccessor();
        LookupLogic lookupLogic = new TransferMessageLookupLogic(receiverId, payload);
        return lookup(receiverId, successor, lookupLogic, "Transfer Message");
    }

    @Override
    public String transferPublicKey(String receiverId, String publicKey64, long messageSessionId) {
        Node successor = getSuccessor();
        LookupLogic lookupLogic = new TransferPublicKeyLookupLogic(receiverId, publicKey64, messageSessionId);
        return lookup(receiverId, successor, lookupLogic, "Transfer Public Key");
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


    public void initConversations(Connection connection) {
        conversationDAO = new ConversationDAO(connection, secretKey);
        final List<Conversation> allConversations = conversationDAO.getAllConversations(getId());
        this.conversations.addAll(allConversations);
    }


    public void setSecretKey(SecretKey secretKey) {
        this.secretKey = secretKey;
    }
}
