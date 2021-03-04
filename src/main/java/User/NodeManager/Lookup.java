package User.NodeManager;

import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

import static User.NodeManager.NodeUtil.isBigger;

public class Lookup implements Callable<String> {
    private static final Logger LOGGER = Logger.getLogger(Lookup.class.getName());
    private final User user;
    private final String lookUpId;

    public Lookup(User user, String lookUpId) {
        this.user = user;
        this.lookUpId = lookUpId;
    }


    @Override
    public String call() {
        //System.out.println("Node " + this.id + " is looking for node " + id);
        try {
//            System.out.println(this.id + " START LOOKUP id " + id);

            final Node successor = user.getSuccessor();
            if (isBigger(lookUpId, user.getId()) && (isBigger(successor.getId(), lookUpId)) || user.getId().equals(lookUpId)) {
//            System.out.println("node " + this.id + "returns its successor" + getSuccessor().getId());
                return successor.getJSONString();
            } else {
                final Node highestPredecessor = findHighestPredecessor(lookUpId);
//                System.out.println("Highest predecessor is node " + highestPredecessor.getId());
//            System.out.println("highest successor is " + highestPredecessor.getId());
                //if highestPredecessor is the MAX node
                if (highestPredecessor.equals(user)) {
//                System.out.println("node " + this.id + " is a MAX node, returning its successor - min node " + highestPredecessor.getSuccessor().getId());
                    //if ID is either new MIN or MAX node then return current MIN node as ID's successor
                    if (isBigger(lookUpId, user.getId()) || isBigger(successor.getId(), lookUpId)) {
                        return user.getSuccessor().getJSONString();
                    }
                    /* if we are here, then current node is a MAX node which has only one connection in its finger table(successor only),
                     and ID is a NORMAL node; then we go to current node's successor;
                     */
//                    return null;
                    return user.getSuccessor().lookUp(lookUpId).get(); //TODO UNCOMENT transfer lookp
                }
                //if highest predecessor is the normal node then continues look up.
//            System.out.println("node " + this.getId() + "sending lookup of node " + id + " to highest predecessor " + highestPredecessor.getId());
//                return null;
                return highestPredecessor.lookUp(lookUpId).get(); // TODO UNCOMENT Transfer lookup
            }
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.warning("Unable to receive reply form message session. Reason: " + e.toString());
            return null;
        } catch (Exception e) {
            LOGGER.warning("Lookup not found. Reason: " + e.toString());
            return null;
        }
    }

    private Node findHighestPredecessor(String id) {
        final Set<String> userConnections = user.getNodes().keySet();
        final int size = userConnections.size();
        final String[] arr = new String[size];
        final Node successor = user.getSuccessor();
        userConnections.toArray(arr);
        int resId = binarySearch(arr, 0, size - 1, id);
//        System.out.println("Highest is " + resId + " was choosen from");
//        for(String s: arr){
//            System.out.println(s);
//        }
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
//        System.out.println("Highest was choosen from");

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
