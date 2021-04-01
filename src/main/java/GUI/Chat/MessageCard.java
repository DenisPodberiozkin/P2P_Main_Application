package GUI.Chat;

import User.NodeManager.Message;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class MessageCard extends ListCell<Message> {

	private static final String MESSAGE_CARD_FXML = "../../fxml/ChatScreen/MessageCard.fxml";
	private static final String DARK_BACKGROUND = "message-card-dark";
	private static final String LIGHT_BACKGROUND = "message-card-light";
	private static final String[] bgColors = {DARK_BACKGROUND, LIGHT_BACKGROUND};

	@FXML
	private AnchorPane msgRoot;

	@FXML
	private AnchorPane msgBackground;

	@FXML
	private Label msgName;

	@FXML
	private Label msgText;

	@FXML
	private Pane usernameWrapper;

	private FXMLLoader loader;


	@Override
	protected void updateItem(Message message, boolean empty) {

		try {
			if (loader == null) {
				loader = new FXMLLoader(getClass().getResource(MESSAGE_CARD_FXML));
				loader.setController(this);
				loader.load();
			}
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}

		super.updateItem(message, empty);
		setText(null);

		if (empty || message == null) {
			setGraphic(null);
		} else {
			msgBackground.getStyleClass().removeAll(bgColors);

			if (message.isSentByUser()) {
				moveMessageCard(NodeOrientation.RIGHT_TO_LEFT, LIGHT_BACKGROUND);
			} else {
				moveMessageCard(NodeOrientation.LEFT_TO_RIGHT, DARK_BACKGROUND);
			}

			msgName.textProperty().unbind();
			msgText.textProperty().unbind();

			msgName.textProperty().bind(message.getSenderNameProperty());
			msgText.textProperty().bind(message.getTextProperty());

			setGraphic(msgRoot);

		}

	}

	private void moveMessageCard(NodeOrientation nodeOrientation, String backGroundColor) {
		setNodeOrientation(nodeOrientation);
		msgRoot.setNodeOrientation(nodeOrientation);
		usernameWrapper.setNodeOrientation(nodeOrientation);
		msgBackground.getStyleClass().add(backGroundColor);
	}
}
