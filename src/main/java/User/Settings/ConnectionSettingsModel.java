package User.Settings;

import User.Database.DAO.ConnectionSettingsDAO;

import java.sql.Connection;

public class ConnectionSettingsModel {
    public static final int LOCAL_SERVER_CONNECTION = 0;
    public static final int EXISTING_USER_CONNECTION = 1;
    public static final int BOTH_CONNECTION = 2;

    public static final int DEFAULT_CONNECTION_TYPE = LOCAL_SERVER_CONNECTION;
    public static final String DEFAULT_LOCAL_SERVER_IP = "0.0.0.0";
    public static final int DEFAULT_LOCAL_SERVER_PORT = 4444;
    public static final String DEFAULT_EXISTING_USER_IP = "0.0.0.0";
    public static final int DEFAULT_EXISTING_USER_PORT = 4444;

    private static int connectionType;
    private static String localServerIp;
    private static int localServerPort;
    private static String existingUserIp;
    private static int existingUserPort;
    private static ConnectionSettingsDAO connectionSettingsDAO;

    public static int getConnectionType() {
        return connectionType;
    }

    public static void setConnectionType(int connectionType) {
        ConnectionSettingsModel.connectionType = connectionType;
        if (connectionSettingsDAO != null) {
            connectionSettingsDAO.setConnectionType(connectionType);
        }
    }

    public static String getLocalServerIp() {
        return localServerIp;
    }

    public static void setLocalServerIp(String localServerIp) {
        ConnectionSettingsModel.localServerIp = localServerIp;
        if (connectionSettingsDAO != null) {
            connectionSettingsDAO.setLocalServerIp(localServerIp);
        }
    }

    public static int getLocalServerPort() {
        return localServerPort;
    }

    public static void setLocalServerPort(int localServerPort) {
        ConnectionSettingsModel.localServerPort = localServerPort;
        if (connectionSettingsDAO != null) {
            connectionSettingsDAO.setLocalServerPort(localServerPort);
        }
    }

    public static String getExistingUserIp() {
        return existingUserIp;
    }

    public static void setExistingUserIp(String existingUserIp) {
        ConnectionSettingsModel.existingUserIp = existingUserIp;
        if (connectionSettingsDAO != null) {
            connectionSettingsDAO.setExistingUserIp(existingUserIp);
        }
    }

    public static int getExistingUserPort() {
        return existingUserPort;
    }

    public static void setExistingUserPort(int existingUserPort) {
        ConnectionSettingsModel.existingUserPort = existingUserPort;
        if (connectionSettingsDAO != null) {
            connectionSettingsDAO.setExistingUserPort(existingUserPort);
        }
    }

    public static void initConnectionSettings(Connection connection) {
        connectionSettingsDAO = new ConnectionSettingsDAO(connection);

        connectionType = connectionSettingsDAO.getConnectionType();
        localServerIp = connectionSettingsDAO.getLocalServerIp();
        localServerPort = connectionSettingsDAO.getLocalServerPort();
        existingUserIp = connectionSettingsDAO.getExistingUserIp();
        existingUserPort = connectionSettingsDAO.getExistingUserPort();
    }
}
