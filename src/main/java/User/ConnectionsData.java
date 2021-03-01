package User;

public class ConnectionsData {
    private static final int LOCAL_SERVER_PORT = 4444;
    private static final String LOCAL_SERVER_IP = "localhost";
    private static int USER_SERVER_PORT;

    public static int getUserServerPort() {
        return USER_SERVER_PORT;
    }

    public static void setUserServerPort(int userServerPort) {
        USER_SERVER_PORT = userServerPort;
    }

    public static int getLocalServerPort() {
        return LOCAL_SERVER_PORT;
    }

    public static String getLocalServerIp() {
        return LOCAL_SERVER_IP;
    }
}
