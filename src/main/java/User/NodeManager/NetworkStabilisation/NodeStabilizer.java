package User.NodeManager.NetworkStabilisation;

import User.NodeManager.Node;
import User.NodeManager.User;

import java.util.concurrent.Callable;

import static User.NodeManager.NodeUtil.isBigger;

public class NodeStabilizer implements Callable<Boolean> {
    private final User user;

    public NodeStabilizer(User user) {
        this.user = user;
    }


    @Override
    public Boolean call() {
        Node successor = user.getSuccessor();
        if (user.hasNeighbours()) {

            final Node x = successor.getPredecessor();
            if (x == null) {
                successor.notifyAboutNewPredecessor(user);
                return false;
            }

            if (!user.equals(x)) {


                if (isBigger(x, user) && isBigger(successor, x)) { // Normal Case
                    user.setSuccessorAndConnect(x);
                } else if (isBigger(user, successor)) { //verifies than this node is current max
                    // MIN or MAX case
                    if (isBigger(successor, x) /*then x is the new min */ ||
                            isBigger(x, user) /*then this node is max */) {
                        user.setSuccessorAndConnect(x);
                    }
                }
            }
            successor = user.getSuccessor();
            user.updateSuccessorsList(successor);
        }

        return true;
    }


}
