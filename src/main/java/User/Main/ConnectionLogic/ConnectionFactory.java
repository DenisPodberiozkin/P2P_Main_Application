package User.Main.ConnectionLogic;

import User.Settings.ConnectionSettingsModel;

public class ConnectionFactory {

	public static ConnectionLogic getConnectionLogic(int connectionType) {
		switch (connectionType) {
			case ConnectionSettingsModel.LOCAL_SERVER_CONNECTION:
				return new ConnectToLocalServer();
			case ConnectionSettingsModel.EXISTING_USER_CONNECTION:
				return new ConnectToExistingUser();
			case ConnectionSettingsModel.BOTH_CONNECTION:
				return new ConnectToBoth();
			default:
				throw new IllegalStateException("Unexpected value: " + connectionType);
		}
	}
}
