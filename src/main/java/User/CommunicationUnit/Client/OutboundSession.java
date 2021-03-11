package User.CommunicationUnit.Client;

import Encryption.EncryptionController;
import Encryption.IEncryptionController;
import User.CommunicationUnit.Server.InboundTokens;
import User.CommunicationUnit.SynchronisedWriter;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

public class OutboundSession implements Callable<String> {
    private static final Logger LOGGER = Logger.getLogger(OutboundSession.class.getName());
    private static int ID = 0;
    private final SynchronisedWriter writer;
    private final String message;
    private final int id;
    private final CountDownLatch latch;
    private final OutboundConnection connection;
    private final IEncryptionController encryptionController = EncryptionController.getInstance();
    private String messageReply = "";
    private final boolean isEncrypted;

    public OutboundSession(SynchronisedWriter writer, String message, OutboundConnection connection, boolean isEncrypted) {
        this.writer = writer;
        this.message = message;
        this.connection = connection;
        this.isEncrypted = isEncrypted;
        this.id = initId();
        this.latch = new CountDownLatch(1);
        if (message.contains(InboundTokens.PING.getToken())) {
            LOGGER.config("Outbound Session " + this.id + " is created to send message: " + this.message + " from connection " + connection.toString() + " to " + this.connection.getIp() + ":" + this.connection.getPort());

        } else {
            LOGGER.info("Outbound Session " + this.id + " is created to send message: " + this.message + " from connection " + connection.toString() + " to " + this.connection.getIp() + ":" + this.connection.getPort());
        }

    }

    private static synchronized int initId() {
        return ID++;
    }

    @Override
    public String call() throws InterruptedException, IllegalArgumentException {

        if (message.equals(InboundTokens.PING.getToken())) {
            LOGGER.fine("Sending message " + message + " to " + connection.getIp() + ":" + connection.getPort() + " from session " + id);
        } else {
            LOGGER.config("Sending message " + message + " to " + connection.getIp() + ":" + connection.getPort() + " from session " + id);
        }


        try {
            String messageToSend;
            if (isEncrypted) {
                messageToSend = encryptionController.encryptStringByAES(connection.getSecretKey(), message);
            } else {
                messageToSend = message;
            }

            writer.sendMessage(id, messageToSend);
            latch.await();

            StringBuilder editedMessage = new StringBuilder();
            String[] tokens = messageReply.split(" ");
            for (int i = 1; i < tokens.length; i++) {
                editedMessage.append(tokens[i]);
                if (i != tokens.length - 1) {
                    editedMessage.append(" ");
                }
            }
            messageReply = editedMessage.toString();

            if (isEncrypted) {
                messageReply = encryptionController.decryptStringByAES(connection.getSecretKey(), messageReply);
            }

            if (message.contains(InboundTokens.PING.getToken())) {
                LOGGER.config("Outbound Session " + id + " received reply " + messageReply + " from" + connection.getIp() + ":" + connection.getPort());
            } else {
                LOGGER.info("Outbound Session " + id + " received reply " + messageReply + " from" + connection.getIp() + ":" + connection.getPort());
            }


            if (messageReply.contains("ERROR")) {
                LOGGER.warning("Outbound Session " + id + " received " + messageReply + " in response to message: " + message + " sent to " + connection.getIp() + ":" + connection.getPort());
                throw new IllegalArgumentException("REQUEST ERROR " + messageReply);
            }
            return messageReply;
        } finally {
            connection.closeSession(this.id);
        }
    }


    public void notifyReply(String message) {
        this.messageReply = message;
        latch.countDown();
    }

    public int getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }
}
