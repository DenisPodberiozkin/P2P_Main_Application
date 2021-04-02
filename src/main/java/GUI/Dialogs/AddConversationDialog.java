package GUI.Dialogs;

import GUI.Chat.AddConversationDialogContent;
import GUI.ControllerFactory;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.util.Pair;

public class AddConversationDialog extends AbstractDialog {
	private final static String ADD_CONVERSATION_DIALOG_CONTENT_FXML = "../../fxml/ChatScreen/AddConversationDialogContent.fxml";

	public AddConversationDialog() {
		super(ADD_CONVERSATION_DIALOG_CONTENT_FXML);

		setTitle("Add new Dialog");
		final AddConversationDialogContent addConversationDialogContentController = ControllerFactory.getAddConversationDialogContentController();

		final ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
		getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);
		final Node addButton = getDialogPane().lookupButton(addButtonType);
		addButton.setDisable(true);
		final TextField recipientIdField = addConversationDialogContentController.getRecipientIdField();
		final TextField conversationNameField = addConversationDialogContentController.getConversationNameField();
		//if recipient id is empty then add button is disabled.
		recipientIdField.textProperty().addListener((observable, oldValue, newValue) -> addButton.setDisable(newValue.trim().isEmpty()));

		// dialog is result is converted to pair that contains recipient ID and conversation name
		setResultConverter(button -> {
			try {
				if (button == addButtonType) {
					final String recipientId = recipientIdField.getText();
					final String conversationNameFieldText = conversationNameField.getText();
					return new Pair<>(recipientId, conversationNameFieldText);
				}
				return null;
			} finally {
				recipientIdField.clear();
				conversationNameField.clear();
			}
		});


	}
}
