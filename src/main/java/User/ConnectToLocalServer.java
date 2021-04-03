package User;

import User.CommunicationUnit.Client.ClientController;
import User.CommunicationUnit.Client.IClientController;
import User.CommunicationUnit.Client.OutboundConnection;
import User.NodeManager.Node;
import User.NodeManager.User;
import User.Settings.ConnectionSettingsModel;

import java.io.IOException;
import java.util.logging.Logger;

public class ConnectToLocalServer implements ConnectionLogic {
	private static final Logger LOGGER = Logger.getLogger(ConnectToLocalServer.class.getName());
	private final IClientController clientController = ClientController.getInstance();

	@Override
	public void connect() throws IOException {

		try (OutboundConnection serverConnection = clientController.connect(ConnectionSettingsModel.getLocalServerIp(), ConnectionSettingsModel.getLocalServerPort())) {

			User user = User.getInstance();
			boolean isLastConnectedNodeReachable = false;
			boolean isLastConnectedNodePresent = false;
			do {
				Node lastConnectedNode = clientController.getLastConnectedNode(serverConnection);
				if (user != null) {
					if (lastConnectedNode != null) {
						isLastConnectedNodePresent = true;
						try {
							lastConnectedNode.connectToNode();
							isLastConnectedNodeReachable = true;
							if (lastConnectedNode.hasNeighbours()) {
								final String successorJson = lastConnectedNode.findNode(user.getId());
								if (successorJson.equals("NF")) {
									throw new IOException("Successor not found");
								}
								Node successorNode = Node.getNodeFromJSONSting(successorJson);
								lastConnectedNode.closeConnection();
								user.join(successorNode);
							} else {
								user.join(lastConnectedNode);
							}
						} catch (IOException ioException) {
							LOGGER.warning("Unable to connect to last node. Reason " + ioException.toString());
							isLastConnectedNodeReachable = false;
							clientController.removeUnreachableLastConnectedNode(serverConnection, lastConnectedNode.getJSONString());
						}
					} else {
						if (!isLastConnectedNodePresent) {
							user.join();
						} else {
							LOGGER.warning("ALL last connected nodes are unreachable. Creating the new Sub-ring");
							user.join();
							isLastConnectedNodePresent = false;
						}
					}
				} else {
					LOGGER.warning("User undefined");
				}
			} while (isLastConnectedNodePresent && !isLastConnectedNodeReachable);
			clientController.sendLastNodeToServer(serverConnection, user);

		} catch (IOException ioException) {
			LOGGER.warning("Unable to connect to local server. Reason " + ioException.toString());
			throw new IOException("Unable to connect to to local server");
		}
	}
}
