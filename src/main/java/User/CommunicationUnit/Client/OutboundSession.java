package User.CommunicationUnit.Client;

import User.CommunicationUnit.SynchronisedWriter;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

public class OutboundSession implements Callable<String> {

    private static int ID = 0;
    private final SynchronisedWriter writer;
    private final String message;
    private final int id;
    private final CountDownLatch latch;
    private final OutboundConnection connection;
    private String messageReply = "";

    public OutboundSession(SynchronisedWriter writer, String message, OutboundConnection connection) {
        this.writer = writer;
        this.message = message;
        this.connection = connection;
        this.id = ID++;
        this.latch = new CountDownLatch(1);
    }

    @Override
    public String call() throws InterruptedException {
        writer.sendReply(id, message);
        latch.await();
        StringBuilder editedMessage = new StringBuilder();
        String[] tokens = messageReply.split(" ");
        for (int i = 1; i < tokens.length; i++) {
            editedMessage.append(tokens[i]);
            if (i != tokens.length - 1) {
                editedMessage.append(" ");
            }
        }
        return editedMessage.toString();
    }


    public void notifyReply(String message) {
        connection.closeSession(this.id);
        this.messageReply = message;
        latch.countDown();
    }

    public int getId() {
        return id;
    }
}
