package GUI.Chat;

import GUI.ControllerFactory;
import GUI.Dialogs.TextInputDialog;
import User.NodeManager.Conversation;
import User.NodeManager.Message;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class ConversationScreen implements Initializable {

	@FXML
	private TextArea sendField;

	@FXML
	private Label chatTitle;

	@FXML
	private ListView<Message> chatList;

	private Conversation conversation;


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		ControllerFactory.setConversationScreenController(this);
		chatList.setSelectionModel(new NoSelectionModel<>());
		chatList.setCellFactory(messageCard -> new MessageCard());

		// send on enter, new line on SHIFT + ENTER
		sendField.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.ENTER) {
				event.consume();
				if (event.isShiftDown()) {
					sendField.appendText(System.getProperty("line.separator"));
				} else {
					if (!sendField.getText().isEmpty()) {
						sendBtnAction();
					}
				}
			}
		});
	}


	@FXML
	void editConversationName() {
		TextInputDialog textInputDialog = new TextInputDialog("Conversation edition", "Change conversation name to the new?", "Enter the new conversation name");
		Optional<String> result = textInputDialog.showAndWait();
		result.ifPresent(conversationName -> conversation.setConversationName(conversationName));
	}

	@FXML
	void sendBtnAction() {
		conversation.addMessage(sendField.getText(), "123", true); //TODO chanhe later
		sendField.clear();
	}

	private void updateContent() {
		chatList.setItems(conversation.getMessages());
		sendField.setText(conversation.getDraftMessage());
	}

	private void updateBindings(Conversation newConversation) {
		if (conversation != null) {
			chatTitle.textProperty().unbindBidirectional(conversation.getConversationNameProperty());
		}
		chatTitle.textProperty().bindBidirectional(newConversation.getConversationNameProperty());
	}

	public void updateConversationScreenContent(Conversation conversation) {
		final String draftMessage = sendField.getText();
		if (draftMessage != null && this.conversation != null) {
			this.conversation.setDraftMessage(draftMessage);
		}
		updateBindings(conversation);
		this.conversation = conversation;
		updateContent();
	}
}
