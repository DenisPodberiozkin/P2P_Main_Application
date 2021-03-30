package User.Settings;

import User.Database.DAO.ApplicationSettingsDAO;

import java.sql.Connection;

public class ApplicationSettingsModel {
	public static final int DEFAULT_APPLICATION_PORT = 4444;

	private static int applicationPort;
	private static ApplicationSettingsDAO applicationSettingsDAO;


	public static int getApplicationPort() {
		return applicationPort;
	}

	public static void setApplicationSettingsDAO(ApplicationSettingsDAO applicationSettingsDAO) {
		ApplicationSettingsModel.applicationSettingsDAO = applicationSettingsDAO;
	}

	public static void setApplicationPort(int applicationPort) {
		ApplicationSettingsModel.applicationPort = applicationPort;
		if (applicationSettingsDAO != null) {
			applicationSettingsDAO.setApplicationPort(applicationPort);
		}
	}

	public static void initSettings(Connection connection) {
		applicationSettingsDAO = new ApplicationSettingsDAO(connection);
		applicationPort = applicationSettingsDAO.getApplicationPort();
	}

}
