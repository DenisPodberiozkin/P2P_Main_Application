package User.Database;

import User.Settings.ApplicationSettingsModel;
import User.Settings.ConnectionSettingsModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class SettingsDataBase extends Database {
	public SettingsDataBase() {
		super("src/main/resources/db/");
	}

	public Connection connectToSettingsDatabase(String dbName) throws SQLException, FileNotFoundException {
		String fullPath = path + dbName + ".db";
		try {
			super.connect(fullPath);
		} catch (FileNotFoundException e) {
			createDb(dbName);
			super.connect(fullPath);
			initDb(connection);
		}
		return connection;
	}

	private void createDb(String dbName) {
		try {
			final File file = new File(path + dbName + ".db");
			file.createNewFile();
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}


	private void initDb(Connection connection) throws SQLException {
		final String applicationSettingsSQL =
				"create table Application_Settings\n" +
						"(\n" +
						"    application_port INTEGER default " + ApplicationSettingsModel.DEFAULT_APPLICATION_PORT + " not null\n" +
						");";
		final String connectionSettingsSQL =
				"create table Connection_Settings\n" +
						"(\n" +
						"    connection_type    INTEGER default " + ConnectionSettingsModel.DEFAULT_CONNECTION_TYPE + " not null,\n" +
						"    local_server_ip    STRING  default '" + ConnectionSettingsModel.DEFAULT_LOCAL_SERVER_IP + "' not null,\n" +
						"    local_server_port  INTEGER default " + ConnectionSettingsModel.DEFAULT_LOCAL_SERVER_PORT + " not null,\n" +
						"    existing_user_ip   STRING  default '" + ConnectionSettingsModel.DEFAULT_EXISTING_USER_IP + "' not null,\n" +
						"    existing_user_port INTEGER default " + ConnectionSettingsModel.DEFAULT_EXISTING_USER_PORT + " not null\n" +
						");";

		final String applicationSettingsDefaultsSQL = "INSERT INTO Application_Settings DEFAULT VALUES;";
		final String connectionSettingsDefaultsSQL = "INSERT INTO Connection_Settings DEFAULT VALUES;";

		try (Statement statement = connection.createStatement()) {
			statement.execute(applicationSettingsSQL);
			statement.execute(connectionSettingsSQL);
			statement.executeUpdate(applicationSettingsDefaultsSQL);
			statement.executeUpdate(connectionSettingsDefaultsSQL);
		}
	}


}
