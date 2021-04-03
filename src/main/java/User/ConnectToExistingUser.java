package User;

import User.NodeManager.Node;
import User.NodeManager.User;
import User.Settings.ConnectionSettingsModel;

import java.io.IOException;

public class ConnectToExistingUser implements ConnectionLogic {

	@Override
	public void connect() throws IOException {
		final User user = User.getInstance();
		Node existingUserNode = new Node(ConnectionSettingsModel.getExistingUserIp(), ConnectionSettingsModel.getExistingUserPort());
		try {
			existingUserNode.connectToNode();
			existingUserNode.initNodeInformation();
			if (existingUserNode.hasNeighbours()) {
				final Node successorNode = Node.getNodeFromJSONSting(existingUserNode.findNode(user.getId()));
				existingUserNode.closeConnection();
				user.join(successorNode);
			} else {
				user.join(existingUserNode);
			}
		} catch (IOException ioException) {
			throw new IOException("Existing user is unreachable");
		}
	}
}
