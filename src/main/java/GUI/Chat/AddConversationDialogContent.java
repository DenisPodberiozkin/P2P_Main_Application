package GUI.Chat;

import GUI.ControllerFactory;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class AddConversationDialogContent implements Initializable {
	@FXML
	private TextField recipientIdField;

	@FXML
	private TextField conversationNameField;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		ControllerFactory.setAddConversationDialogContentController(this);
	}


	public TextField getRecipientIdField() {
		return recipientIdField;
	}

	public TextField getConversationNameField() {
		return conversationNameField;
	}


}
