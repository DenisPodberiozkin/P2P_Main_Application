package GUI.Chat;

import GUI.ControllerFactory;
import GUI.Debugger;
import GUI.Dialogs.*;
import GUI.GUI_Util;
import GUI.Navigators.ChatScreenNavigator;
import GUI.Navigators.NavigablePane;
import User.NodeManager.Conversation;
import User.NodeManager.Exceptions.ConversationException;
import User.NodeManager.User;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.util.Pair;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class ChatScreen implements Initializable {

	private final UserAccountDialog userAccountDialog = new UserAccountDialog();
	private final User user = User.getInstance();
	private Conversation selectedConversation;


	@FXML
	private TextField searchField;
	@FXML
	private ListView<Conversation> conversationList;
	@FXML
	private AnchorPane chatScreenCenter;
	@FXML
	private ToggleButton debugBtn;


	public ChatScreen() {
//		Conversation conversation1 = new Conversation("123");
//		conversation1.setConversationName("Denis");
//		Conversation conversation2 = new Conversation("123");
//		conversation2.setConversationName("Jegor");
//		Conversation conversation3 = new Conversation("123");
//		conversation3.setConversationName("Mama");
//		Conversation conversation4 = new Conversation("123");
//		conversation4.setConversationName("Papa");
//
//		conversationObservableList.add(conversation1);
//		conversationObservableList.add(conversation2);
//		conversationObservableList.add(conversation3);
//		conversationObservableList.add(conversation4);
//
//		conversation4.addMessage("dnsadhuweifuiwenfuiewnfuiwenfuiwenfuiwenfuiwenfuiwenfuiwe", "dsds", true);
//		conversation4.addMessage("dnsadhuweifuiwenfuiewnfuiwenfuiwenfuiwenfuiwenfuiwenfuiwe", "dsds", false);

//		try {
//			user.createNewConversation("1", "Denis");
//			user.addMessage("1", "555", "Hellpo", true);
//			user.addMessage("1", "555", "Mesg", true);
//			user.addMessage("1", "555", "djsijdis", true);
//			user.addMessage("1", "1", "djsijdis", false);
//			user.createNewConversation("2", "Jegor");
//			user.addMessage("2", "555", "AGA", true);
//			user.addMessage("2", "555", "MANDA", true);
//			user.addMessage("2", "555", "DA?", false);
//			user.addMessage("2", "2", "PIZDA", true);
//			user.createNewConversation("3", "Papa");
//			user.createNewConversation("4", "Mama");
//		} catch (ConversationException e) {
//			e.printStackTrace();
//		}

	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		ControllerFactory.setChatScreenController(this);
		ChatScreenNavigator.setChatScreenController(this);

		// Search filter
		final FilteredList<Conversation> filteredList = new FilteredList<>(user.getConversations(), p -> true);
		//Listener to search text field
		searchField.textProperty().addListener((observable, oldValue, newValue) -> filteredList.setPredicate(conversation -> {
			if (newValue == null || newValue.isEmpty()) {
				return true;
			}
			String lowerCase = newValue.toLowerCase();
			return conversation.getConversationNameProperty().get().toLowerCase().contains(lowerCase);

		}));

		final SortedList<Conversation> sortedList = new SortedList<>(filteredList);
		conversationList.setCellFactory(conversation -> new ConversationCard());
		conversationList.setItems(sortedList);


		// Selection listener
		conversationList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			final Conversation selectedItem = conversationList.getSelectionModel().getSelectedItem();

			if (selectedItem != null && !selectedItem.equals(selectedConversation)) {
				selectedConversation = selectedItem;
				if (chatScreenCenter.getChildren().isEmpty()) {
					ChatScreenNavigator.changeConversationScreen(NavigablePane.CONVERSATION_XML);
				}

				final ConversationScreen conversationScreenController = ControllerFactory.getConversationScreenController();
				conversationScreenController.updateConversationScreenContent(selectedConversation);
			}
		});

	}

	public void enableDebugToggle() {
		debugBtn.setSelected(false);
	}


	public void setConversationScreen(Pane pane) {
		GUI_Util.setChildToParentAnchorPane(chatScreenCenter, pane);
	}


	@FXML
	void addConversation() {
		Optional<Pair<String, String>> result = new AddConversationDialog().showAndWait();
		if (result.isPresent()) {
			try {
				final String conversationName = result.get().getValue();
				final String recipientId = result.get().getKey();
				if (conversationName.isEmpty()) {
					user.createNewConversation(recipientId);
				} else {
					user.createNewConversation(recipientId, conversationName);
				}
			} catch (ConversationException e) {
				new ErrorAlert("Conversation Error", "Unable to add new conversation", "Unable to add new conversation. Reason - " + e.getMessage()).show();
			}
		}
	}

	@FXML
	void deleteConversation() {
		final Conversation selectedItem = conversationList.getSelectionModel().getSelectedItem();
		if (selectedItem != null) {
			new ConfirmationAlert("Delete conversation?", "Delete conversation - " + selectedItem.getConversationNameProperty().get() + "?", "If you delete this conversation, all related messages will be deleted forever.\nIt is impossible to restore them after deletion.\nDo you want to proceed?")
					.showAndWait()
					.filter(result -> result == ButtonType.OK).
					ifPresent(result -> {
						user.removeConversation(selectedItem);
						chatScreenCenter.getChildren().clear();
						clearSelection();

					});
		} else {
			new WarningAlert("Conversation Warning", "No selected conversation", "Please select conversation to delete it").show();
		}
	}

	private void clearSelection() {
		conversationList.getSelectionModel().clearSelection();
		this.selectedConversation = null;
	}

	@FXML
	void showDebug() {
		Debugger debuggerController = ControllerFactory.getDebuggerController();
		if (debugBtn.isSelected()) {
			debuggerController.showDebug();
		} else {
			debuggerController.hideDebug();
		}
	}

	@FXML
	void showUserAccount() {
		userAccountDialog.show();
	}
}
