package User.CommunicationUnit.Server;

import User.CommunicationUnit.MessageReader;
import User.CommunicationUnit.SynchronisedWriter;
import User.Encryption.Hash;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class InboundConnection extends Thread {
    private static final Logger LOGGER = Logger.getLogger(InboundConnection.class.getName());
    private static final int MAX_THREAD = Hash.getHashSize();
    private final ExecutorService executor;
    private final Socket clientSocket;
    private final HashMap<Integer, InboundSession> sessions;
    private final Server server;
    private final HeartBeatManager heartBeatManager;
    private final int port;
    private final String ip;
    private boolean isConnectionOpen;


    public InboundConnection(Socket clientSocket, Server server) {
        this.clientSocket = clientSocket;
        this.server = server;
        this.isConnectionOpen = true;
        this.executor = Executors.newFixedThreadPool(MAX_THREAD);
        this.sessions = new HashMap<>();
        this.heartBeatManager = new HeartBeatManager(this);
        this.port = clientSocket.getPort();
        this.ip = clientSocket.getInetAddress().toString();
    }

    @Override
    public void run() {
        LOGGER.info("User " + clientSocket.getInetAddress() + ":" + clientSocket.getPort() + " has connected");

        try (MessageReader reader = new MessageReader(new InputStreamReader(clientSocket.getInputStream()));
             SynchronisedWriter writer = new SynchronisedWriter(clientSocket.getOutputStream(), true)) {
            new Thread(heartBeatManager).start();
            while (isConnectionOpen) {
                String request = reader.readLine();
                if (request != null) {
                    String[] tokens = request.split(" ");
                    InboundSession inboundSession = new InboundSession(tokens, writer, this);
                    this.sessions.put(inboundSession.getId(), inboundSession);
                    executor.execute(inboundSession);
                } else {
                    this.isConnectionOpen = false;
                }

            }

        } catch (IOException e) {
            this.isConnectionOpen = false;
        } finally {
            closeConnection();
        }


    }

    public void closeConnection() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5000, TimeUnit.MILLISECONDS)) {
                executor.shutdownNow();
            }

        } catch (InterruptedException e) {
            executor.shutdownNow();
        }

        try {
            LOGGER.info(clientSocket.getInetAddress() + ":" + clientSocket.getPort() + " disconnected");
            if (!clientSocket.isClosed()) {
                clientSocket.close();
            }
            server.removeConnection(this);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeSession(int id) {
        if (this.sessions.get(id).getRequest() == InboundTokens.PING) {
            LOGGER.config("Inbound Session " + id + " was closed");
        } else {
            LOGGER.info("Inbound Session " + id + " was closed");
        }
        this.sessions.remove(id);
    }

    public HeartBeatManager getHeartBeatManager() {
        return heartBeatManager;
    }

    public boolean isConnectionOpen() {
        return isConnectionOpen;
    }

    public int getPort() {
        return port;
    }

    public String getIp() {
        return ip;
    }
}
