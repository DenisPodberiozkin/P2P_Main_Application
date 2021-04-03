package User.NodeManager;

import java.util.LinkedHashMap;
import java.util.concurrent.ExecutionException;

public class Updater implements Runnable {
    private final User user;

    public Updater(User user) {
        this.user = user;
    }


    @Override
    public void run() {
        try {
            while (true) {
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


}
