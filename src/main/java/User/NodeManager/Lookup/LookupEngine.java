package User.NodeManager.Lookup;

import User.NodeManager.Node;
import User.NodeManager.User;

import java.util.Set;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

import static User.NodeManager.NodeUtil.isBigger;

public class LookupEngine implements Callable<String> {
    private static final Logger LOGGER = Logger.getLogger(LookupEngine.class.getName());
    private final User user;
    private final String lookUpId;
    private final Node successor;
    private final LookupLogic lookupLogic;

    public LookupEngine(User user, String lookUpId, Node successor, LookupLogic lookupLogic) {
        this.user = user;
        this.lookUpId = lookUpId;
        this.successor = successor;
        this.lookupLogic = lookupLogic;
    }


    @Override
    public String call() {

        try {

            if (lookUpId.equals(successor.getId())) {
                return lookupLogic.proceed(successor);
            }

            if ((isBigger(lookUpId, user.getId()) && (isBigger(successor.getId(), lookUpId))) || user.getId().equals(lookUpId)) {

                return lookupLogic.finish();
            } else {
                final Node highestPredecessor = findHighestPredecessor(lookUpId);

                //if highestPredecessor is the MAX node
                if (highestPredecessor.equals(user)) {

                    //if ID is either new MIN or MAX node then return current MIN node as ID's successor
                    if (isBigger(lookUpId, user.getId()) || isBigger(successor.getId(), lookUpId)) {
                        return lookupLogic.finish();
                    }
                    /* if we are here, then current node is a MAX node which has only one connection in its finger table(successor only),
                     and ID is a NORMAL node; then we go to current node's successor;
                     */


                    return lookupLogic.proceed(successor);

                }
                //if highest predecessor is the normal node then continues look up.
                return lookupLogic.proceed(highestPredecessor);

            }
        } catch (Exception e) {
            LOGGER.warning("Lookup not found. Reason: " + e.toString());
            return "NF";
        }
    }

    private Node findHighestPredecessor(String id) {
        final Set<String> userConnections = user.getNodes().keySet();
        final int size = userConnections.size();
        final String[] arr = new String[size];
        userConnections.toArray(arr);
        int resId = binarySearch(arr, 0, size - 1, id);

        if (resId < 0) { // < 0 if ID either bigger or smaller than every node in the finger table
            /*
            If successor of current node is bigger then we go to the maximum node in the finger table
            If successor of current node is less, it means that current node is the maximum node and successor of current node is the minimum node.
             */
            if (isBigger(successor, user)) {
                // if current node is not max or min then go to the max node in the finger table.
                return user.getNodeFromTable(arr[size - 1]);
            }

            // if the current node is the max node and ID is not new MAX or MIN then go to he highest node in the finger table
            if (isBigger(user.getId(), id) && isBigger(id, successor.getId())) {
                return user.getNodeFromTable(arr[size - 1]);
            }

            // if the current node is the max node and ID is the new MIN or MAX node; then return current node.
            return user;
        }
        return user.getNodeFromTable(arr[resId]);

    }

    private int binarySearch(String[] arr, int l, int r, String x) {


        if (r >= l) {
            int mid = l + (r - l) / 2;
            if (mid != arr.length - 1) {
                if (isBigger(x, arr[mid]) && isBigger(arr[mid + 1], x)) {
                    return mid;
                }


                if (isBigger(arr[mid], x))
                    return binarySearch(arr, l, mid - 1, x);

                return binarySearch(arr, mid + 1, r, x);
            }

        }

        return -1;
    }


}
