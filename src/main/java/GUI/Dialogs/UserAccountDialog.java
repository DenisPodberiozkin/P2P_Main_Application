package GUI.Dialogs;

import User.NodeManager.User;
import javafx.scene.control.ButtonType;

public class UserAccountDialog extends AbstractDialog {
	private final static String USER_ACCOUNT_DIALOG_CONTENT_FXML = "../../fxml/ChatScreen/UserAccountDialogContent.fxml";

	public UserAccountDialog() {
		super(USER_ACCOUNT_DIALOG_CONTENT_FXML);
		User user = User.getInstance();
		setTitle("User account - " + user.getUsername());
		getDialogPane().getButtonTypes().add(ButtonType.OK);
	}
}
