package User.Database.DAO;

import User.Settings.ApplicationSettingsModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class ApplicationSettingsDAO {
	private final Connection connection;

	public ApplicationSettingsDAO(Connection connection) {
		this.connection = connection;
	}

	public int getApplicationPort() {
		final String sql =
				"SELECT application_port\n" +
						"  FROM Application_Settings\n" +
						"  LIMIT 1;\n";
		int port = ApplicationSettingsModel.DEFAULT_APPLICATION_PORT;
		try {
			connection.setAutoCommit(false);
			try (Statement statement = connection.createStatement()) {
				port = statement.executeQuery(sql).getInt("application_port");
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


		return port;
	}

	public void setApplicationPort(int port) {
		final String sql = "" +
				"UPDATE Application_Settings\n" +
				"SET application_port = ?\n" +
				"WHERE application_port IN(\n" +
				"        SELECT application_port FROM Application_Settings LIMIT 1\n" +
				"        );";
		try {
			connection.setAutoCommit(false);
			try (PreparedStatement statement = connection.prepareStatement(sql)) {
				statement.setInt(1, port);
				statement.executeUpdate();
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
}
