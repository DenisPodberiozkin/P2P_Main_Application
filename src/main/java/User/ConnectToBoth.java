package User;

import java.io.IOException;

public class ConnectToBoth implements ConnectionLogic {

	@Override
	public void connect() throws IOException {
		try {
			new ConnectToExistingUser().connect();
		} catch (IOException ioException) {
			try {
				new ConnectToLocalServer().connect();
			} catch (IOException e) {
				throw new IOException("Both existing user and local server are unreachable");
			}
		}
	}
}
