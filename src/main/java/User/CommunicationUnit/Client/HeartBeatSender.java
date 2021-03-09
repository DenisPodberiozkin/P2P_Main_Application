package User.CommunicationUnit.Client;

import User.CommunicationUnit.Server.InboundTokens;

import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

public class HeartBeatSender implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(HeartBeatSender.class.getName());
    private final OutboundConnection connection;
    private boolean isRunning;

    public HeartBeatSender(OutboundConnection connection) {
        this.connection = connection;
        this.isRunning = true;
    }

    @Override
    public void run() {
        try {
//            Thread.sleep(1000);
            while (isRunning) {
                String reply = connection.sendMessage(InboundTokens.PING.getToken(), true).get();
                if (reply.equalsIgnoreCase("PING OK")) {
                    LOGGER.info("Connection to " + connection.getIp() + ":" + connection.getPort() + " is live");
                }
                Thread.sleep(5000);

            }

        } catch (InterruptedException | ExecutionException e) {
            LOGGER.warning("Unable to receive reply from PING message session. Reason: " + e.toString());
        }
    }

    public void stopHeartBeat() {
        this.isRunning = false;
    }
}
