package User.CommunicationUnit.Server;

public class HeartBeatManager implements Runnable {

    private final InboundConnection connection;
    private boolean isRunning;
    private boolean isPingReceived;

    public HeartBeatManager(InboundConnection connection) {
        this.connection = connection;
        this.isRunning = true;
    }

    @Override
    public void run() {
        try {

            while (isRunning) {
                Thread.sleep(15000);
                if (!isPingReceived) {
                    this.isRunning = false;
                } else {
                    this.isPingReceived = false;
                }
            }
            if (connection.isConnectionOpen()) {
                connection.closeConnection();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void pingReceived() {
        this.isPingReceived = true;
    }
}
