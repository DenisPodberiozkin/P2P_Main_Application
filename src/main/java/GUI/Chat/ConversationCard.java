package GUI.Chat;

import User.NodeManager.Conversation;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ConversationCard extends ListCell<Conversation> implements Initializable {

	private final static String CONVERSATION_CARD_XML = "../../fxml/ChatScreen/ConversationCard.fxml";
	private final boolean checked = false;
	@FXML
	private AnchorPane conversationCard;
	@FXML
	private Label nameLabel;
	@FXML
	private Label textLabel;
	private FXMLLoader loader;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}


	@Override
	protected void updateItem(Conversation conversation, boolean empty) {
		try {
			if (loader == null) {
				loader = new FXMLLoader(getClass().getResource(CONVERSATION_CARD_XML));
				loader.setController(this);
				loader.load();

			}
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}

		super.updateItem(conversation, empty);

		if (empty || conversation == null) {
			setGraphic(null);
		} else {
			nameLabel.textProperty().unbind();
			textLabel.textProperty().unbind();
			nameLabel.textProperty().bind(conversation.getConversationNameProperty());
			textLabel.textProperty().bind(conversation.getLastMessageProperty());
			setGraphic(conversationCard);
		}

	}


}
