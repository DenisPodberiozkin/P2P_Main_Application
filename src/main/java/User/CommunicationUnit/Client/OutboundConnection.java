package User.CommunicationUnit.Client;

import User.CommunicationUnit.MessageReader;
import User.CommunicationUnit.SynchronisedWriter;
import User.Encryption.Hash;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

public class OutboundConnection implements AutoCloseable {
    private final static int MAX_THREAD = Hash.getHashSize();
    private final String ip;
    private final int port;
    private final HashMap<Integer, OutboundSession> sessions;
    private final ExecutorService executor;
    private Socket socket;
    private MessageReader reader;
    private SynchronisedWriter writer;
    private ClientReceiver receiver;

    public OutboundConnection(String ip, int port) {
        this.ip = ip;
        this.port = port;
        this.sessions = new HashMap<>();
        this.executor = Executors.newFixedThreadPool(MAX_THREAD);
    }

    @Override
    public void close() throws IOException {
        closeConnection();
    }

    public void createConnection() throws IOException {
        socket = new Socket(ip, port);
        reader = new MessageReader(new InputStreamReader(socket.getInputStream()));
        writer = new SynchronisedWriter(socket.getOutputStream(), true);
        receiver = new ClientReceiver(reader, this);
        new Thread(receiver).start();


    }

    public FutureTask<String> sendMessage(String message) {
        OutboundSession session = new OutboundSession(writer, message, this);
        sessions.put(session.getId(), session);
        return (FutureTask<String>) executor.submit(session);
//        System.out.println(message);
//        writer.println(message);
    }

    public void closeConnection() throws IOException {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5000, TimeUnit.MILLISECONDS)) {
                executor.shutdownNow();
            }

        } catch (InterruptedException e) {
            executor.shutdownNow();
        }

        System.out.println("Close socket");
        OutboundConnectionManager.getInstance().closeConnection(this);
        if (receiver != null) {
            receiver.stopReceiver();
        }

        if (reader != null) {
            reader.close();
        }
        if (writer != null) {
            writer.close();
        }

        if (socket != null) {
            socket.close();
        }

    }

    public void closeSession(int id) {
        System.out.println(id + " removed");
        sessions.remove(id);
    }

    public OutboundSession getSession(int id) {
        return sessions.get(id);
    }

}
