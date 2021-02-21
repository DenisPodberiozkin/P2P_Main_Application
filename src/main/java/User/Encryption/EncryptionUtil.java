package User.Encryption;

public class EncryptionUtil {

    public static String byteToHex(byte[] bytes) {
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
