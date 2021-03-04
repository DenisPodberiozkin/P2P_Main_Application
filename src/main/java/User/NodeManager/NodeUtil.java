package User.NodeManager;

import java.math.BigInteger;

public class NodeUtil {
    public static boolean isBigger(String id1, String id2) {
        return id1.compareTo(id2) > 0;
    }

    public static boolean isBigger(Node n1, Node n2) {
        return n1.getId().compareTo(n2.getId()) > 0;
    }

    public static BigInteger hexToInt(String hex) {
        return new BigInteger(hex, 16);
    }

    public static String byteToHex(BigInteger integer) {
        byte[] bytes = integer.toByteArray();
        while (bytes[0] == 0) {
            byte[] tmp = new byte[bytes.length - 1];
            System.arraycopy(bytes, 1, tmp, 0, tmp.length);
            bytes = tmp;
        }
        StringBuilder hexString = new StringBuilder(2 * bytes.length);

        for (byte b : bytes) {
            hexString.append(String.format("%02X", b));

        }

        return hexString.toString();
    }
}
