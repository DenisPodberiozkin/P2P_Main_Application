package User.Settings;

public class ConnectionSettingsModel {
    public static final int LOCAL_SERVER_CONNECTION = 0;
    public static final int EXISTING_USER_CONNECTION = 1;
    public static final int BOTH_CONNECTION = 2;

    private static int connectionType = LOCAL_SERVER_CONNECTION;
    private static String localServerIp = "0.0.0.0";
    private static int localServerPort = 4444;
    private static String existingUserIp = "0.0.0.0";
    private static int existingUserPort = 4444;

    public static int getConnectionType() {
        return connectionType;
    }

    public static void setConnectionType(int connectionType) {
        ConnectionSettingsModel.connectionType = connectionType;
    }

    public static String getLocalServerIp() {
        return localServerIp;
    }

    public static void setLocalServerIp(String localServerIp) {
        ConnectionSettingsModel.localServerIp = localServerIp;
    }

    public static int getLocalServerPort() {
        return localServerPort;
    }

    public static void setLocalServerPort(int localServerPort) {
        ConnectionSettingsModel.localServerPort = localServerPort;
    }

    public static String getExistingUserIp() {
        return existingUserIp;
    }

    public static void setExistingUserIp(String existingUserIp) {
        ConnectionSettingsModel.existingUserIp = existingUserIp;
    }

    public static int getExistingUserPort() {
        return existingUserPort;
    }

    public static void setExistingUserPort(int existingUserPort) {
        ConnectionSettingsModel.existingUserPort = existingUserPort;
    }
}
