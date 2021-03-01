package User.NodeManager;

import java.util.Set;

public class Lookup {
    private final User user;

    public Lookup(User user) {
        this.user = user;
    }

    public Object lookup(String id) {
        //System.out.println("Node " + this.id + " is looking for node " + id);
        try {
//            System.out.println(this.id + " START LOOKUP id " + id);

            final Node successor = user.getSuccessor();
            if (isBigger(id, user.getId()) && (isBigger(successor.getId(), id)) || user.getId().equals(id)) {
//            System.out.println("node " + this.id + "returns its successor" + getSuccessor().getId());
                return successor;
            } else {
                final Node highestPredecessor = findHighestPredecessor(id);
//                System.out.println("Highest predecessor is node " + highestPredecessor.getId());
//            System.out.println("highest successor is " + highestPredecessor.getId());
                //if highestPredecessor is the MAX node
                if (highestPredecessor.equals(user)) {
//                System.out.println("node " + this.id + " is a MAX node, returning its successor - min node " + highestPredecessor.getSuccessor().getId());
                    //if ID is either new MIN or MAX node then return current MIN node as ID's successor
                    if (isBigger(id, user.getId()) || isBigger(successor.getId(), id)) {
                        return user.getSuccessor();
                    }
                    /* if we are here, then current node is a MAX node which has only one connection in its finger table(successor only),
                     and ID is a NORMAL node; then we go to current node's successor;
                     */
                    return null;
//                    return user.getSuccessor().lookUp(id); //TODO UNCOMENT transfer lookp
                }
                //if highest predecessor is the normal node then continues look up.
//            System.out.println("node " + this.getId() + "sending lookup of node " + id + " to highest predecessor " + highestPredecessor.getId());
                return null;
//                return highestPredecessor.lookUp(id); // TODO UNCOMENT Transfer lookup
            }
        } catch (Exception e) {
            System.err.println("Lookup not found");
            System.err.println(e.getLocalizedMessage());
            System.err.println(e.toString());
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

    public boolean isBigger(String id1, String id2) {
        return id1.compareTo(id2) > 0;
    }

    public boolean isBigger(Node n1, Node n2) {
        return n1.getId().compareTo(n2.getId()) > 0;
    }

}
