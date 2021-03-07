package User.NodeManager;

import java.util.LinkedHashMap;
import java.util.concurrent.ExecutionException;

public class Updater implements Runnable {
    private final User user;
    private boolean isRunning;

    public Updater(User user) {
        this.user = user;
        this.isRunning = true;
    }


    @Override
    public void run() {
        try {
            while (isRunning) {
                Thread.sleep(2000);

                LinkedHashMap<String, Node> updatedNodes = user.checkConnections().get();
                if (updatedNodes != null && updatedNodes.size() > 0) {
                    user.updateFingerTable(updatedNodes);
                }
                if (!user.stabilize().get()) {
                    user.stabilize();
                }

            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

    }


    public void stopUpdater() {
        this.isRunning = false;
    }
}
