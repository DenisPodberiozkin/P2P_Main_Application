package User.Database.DAO;

import User.Settings.ConnectionSettingsModel;

import java.sql.*;


public class ConnectionSettingsDAO {
	private final Connection connection;

	public ConnectionSettingsDAO(Connection connection) {
		this.connection = connection;
	}


	public int getConnectionType() {
		final Object result = getSetting("connection_type");
		return result != null ? (int) result : ConnectionSettingsModel.DEFAULT_CONNECTION_TYPE;
	}

	public void setConnectionType(int connectionType) {
		setSetting("connection_type", connectionType);
	}

	public String getLocalServerIp() {
		final Object result = getSetting("local_server_ip");
		return result != null ? (String) result : ConnectionSettingsModel.DEFAULT_LOCAL_SERVER_IP;
	}

	public void setLocalServerIp(String ip) {
		setSetting("local_server_ip", ip);
	}

	public int getLocalServerPort() {
		final Object result = getSetting("local_server_port");
		return result != null ? (int) result : ConnectionSettingsModel.DEFAULT_LOCAL_SERVER_PORT;
	}

	public void setLocalServerPort(int localServerPort) {
		setSetting("local_server_port", localServerPort);
	}

	public String getExistingUserIp() {
		final Object result = getSetting("existing_user_ip");
		return result != null ? (String) result : ConnectionSettingsModel.DEFAULT_EXISTING_USER_IP;
	}

	public void setExistingUserIp(String ip) {
		setSetting("existing_user_ip", ip);
	}

	public int getExistingUserPort() {
		final Object result = getSetting("existing_user_port");
		return result != null ? (int) result : ConnectionSettingsModel.DEFAULT_EXISTING_USER_PORT;
	}

	public void setExistingUserPort(int port) {
		setSetting("existing_user_port", port);
	}

	private void setSetting(String settingType, Object data) {
		String sql = "UPDATE Connection_Settings " +
				"SET " + settingType + " = ?" +
				"WHERE connection_type " +
				"IN(SELECT connection_type FROM Connection_Settings LIMIT 1);";

		try {
			connection.setAutoCommit(false);
			try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
				if (data instanceof String) {
					preparedStatement.setString(1, (String) data);
				} else if (data instanceof Integer) {
					preparedStatement.setInt(1, (Integer) data);
				} else {
					throw new SQLException("Unsupported insertion Type");
				}
				preparedStatement.executeUpdate();
				connection.commit();
			} catch (SQLException throwables) {
				throwables.printStackTrace();

				connection.rollback();

			} finally {

				connection.setAutoCommit(true);

			}
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}


	}


	private Object getSetting(String settingType) {
		final String sql = "SELECT " + settingType + " FROM Connection_Settings LIMIT 1";

		try {
			connection.setAutoCommit(false);
			try (Statement statement = connection.createStatement()) {
				ResultSet resultSet = statement.executeQuery(sql);
				connection.commit();
				return resultSet.getObject(settingType);
			} catch (SQLException throwables) {
				throwables.printStackTrace();

				connection.rollback();

			} finally {

				connection.setAutoCommit(true);

			}
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}


		return null; // TODO change later;
	}

}
