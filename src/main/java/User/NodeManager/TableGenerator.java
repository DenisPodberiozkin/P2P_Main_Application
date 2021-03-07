package User.NodeManager;

import User.Encryption.Hash;

import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.concurrent.Callable;

public class TableGenerator implements Callable<LinkedHashMap<String, Node>> {
    private final User user;

    public TableGenerator(User user) {
        this.user = user;
    }

    @Override
    public LinkedHashMap<String, Node> call() {
//            System.out.println("Node " + id + " started checking connections");
        BigInteger id = NodeUtil.hexToInt(user.getId());
        String hex;
        LinkedHashMap<String, Node> temp = new LinkedHashMap<>();
        int keySize = Hash.getHashSize();
        for (int i = 0; i < keySize; i++) {
            BigInteger base = BigInteger.valueOf(2);
            BigInteger offset = base.pow(i);
            BigInteger candidateId = id.add(offset);
            BigInteger maxId = base.pow(keySize);
            BigInteger result = candidateId.mod(maxId);
            hex = NodeUtil.byteToHex(result);
//            System.out.println("Node " + id + " looking for node " + hex + " with initial int " + result);
            String candidateNodeJSON = user.lookUp(hex);
            if (candidateNodeJSON != null && !candidateNodeJSON.equals("NF")) {

                Node candidate = Node.getNodeFromJSONSting(candidateNodeJSON);


                if (!candidate.equals(user)) {
                    temp.put(candidate.getId(), candidate);
                }
            }
        }

        return temp;
    }
}

