package GUI.Chat;

import GUI.ControllerFactory;
import GUI.Dialogs.ErrorAlert;
import GUI.Dialogs.TextInputPopUp;
import User.NodeManager.Conversation;
import User.NodeManager.Exceptions.MessageException;
import User.NodeManager.Exceptions.SecureMessageChannelException;
import User.NodeManager.Message;
import User.NodeManager.User;
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
		TextInputPopUp textInputPopUp = new TextInputPopUp("Conversation edition", "Change conversation name to the new?", "Enter the new conversation name");
		Optional<String> result = textInputPopUp.showAndWait();
		result.ifPresent(conversationName -> conversation.setConversationName(conversationName));
	}

	@FXML
	void sendBtnAction() {
		User user = User.getInstance();
		try {
			user.sendMessage(conversation.getParticipantId(), sendField.getText());
			chatList.scrollTo(chatList.getItems().size());
			sendField.clear();
		} catch (MessageException e) {
			new ErrorAlert("Message error",
					"Message could not reach its destination",
					"Message was not delivered to the recipient - " +
							conversation.getConversationNameProperty().get() +
							" . Reason - " + e.getMessage())
					.show();
		} catch (SecureMessageChannelException e) {
			new ErrorAlert("Secure Message Session Error",
					"Unable to create secure message session",
					"Secure message session with recipient - " +
							conversation.getConversationNameProperty().get() +
							" could not be established. Reason - " + e.getMessage())
					.show();

		}
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
		chatList.scrollTo(chatList.getItems().size());
	}
}
