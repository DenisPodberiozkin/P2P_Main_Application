package GUI.Chat;

import User.CommunicationUnit.Server.Server;
import User.CommunicationUnit.Server.ServerController;
import User.NodeManager.User;
import User.Settings.ApplicationSettingsModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class UserAccountDialogContent implements Initializable {

	@FXML
	private TextField applicationIpField;

	@FXML
	private TextField applicationPortField;

	@FXML
	private TextField idField;

	@FXML
	private Label usernameLabel;


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		final User user = User.getInstance();
		applicationIpField.setText(ApplicationSettingsModel.getApplicationIp());
		final Server server = ServerController.getInstance().getServer();
		applicationPortField.textProperty().bind(server.getPortProperty().asString());
		idField.setText(user.getId());
		usernameLabel.setText(user.getUsername());
	}
}
