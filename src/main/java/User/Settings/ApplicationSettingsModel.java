package User.Settings;

import User.Database.DAO.ApplicationSettingsDAO;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.sql.Connection;

public class ApplicationSettingsModel {
	public static final int DEFAULT_APPLICATION_PORT = 0;

	private static int applicationPort;
	private static final String applicationIp = initIp();
	private static ApplicationSettingsDAO applicationSettingsDAO;


	public static int getApplicationPort() {
		return applicationPort;
	}

	public static void setApplicationPort(int applicationPort) {
		ApplicationSettingsModel.applicationPort = applicationPort;
		if (applicationSettingsDAO != null) {
			applicationSettingsDAO.setApplicationPort(applicationPort);
		}
	}

	public static String getApplicationIp() {
		return applicationIp;
	}

	public static void initSettings(Connection connection) {
		applicationSettingsDAO = new ApplicationSettingsDAO(connection);
		applicationPort = applicationSettingsDAO.getApplicationPort();
	}

	private static String initIp() {
		try (final DatagramSocket socket = new DatagramSocket()) {
			socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
			return socket.getLocalAddress().getHostAddress();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return "0.0.0.0";
	}

}
