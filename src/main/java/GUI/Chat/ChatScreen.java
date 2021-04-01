package GUI.Chat;

import GUI.ControllerFactory;
import GUI.GUI_Util;
import GUI.Navigators.ChatScreenNavigator;
import GUI.Navigators.NavigablePane;
import User.NodeManager.Conversation;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.ResourceBundle;

public class ChatScreen implements Initializable {

	private final ObservableList<Conversation> conversationObservableList = FXCollections.observableArrayList();
	@FXML
	private TextField searchField;
	@FXML
	private ListView<Conversation> conversationList;
	@FXML
	private AnchorPane chatScreenCenter;
	private Conversation selectedConversation;

	public ChatScreen() {
		Conversation conversation1 = new Conversation("123");
		conversation1.setConversationName("Denis");
		Conversation conversation2 = new Conversation("123");
		conversation2.setConversationName("Jegor");
		Conversation conversation3 = new Conversation("123");
		conversation3.setConversationName("Mama");
		Conversation conversation4 = new Conversation("123");
		conversation4.setConversationName("Papa");

		conversationObservableList.add(conversation1);
		conversationObservableList.add(conversation2);
		conversationObservableList.add(conversation3);
		conversationObservableList.add(conversation4);

		conversation4.addMessage("dnsadhuweifuiwenfuiewnfuiwenfuiwenfuiwenfuiwenfuiwenfuiwe", "dsds", true);
		conversation4.addMessage("dnsadhuweifuiwenfuiewnfuiwenfuiwenfuiwenfuiwenfuiwenfuiwe", "dsds", false);


	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		ControllerFactory.setChatScreenController(this);
		ChatScreenNavigator.setChatScreenController(this);

		final FilteredList<Conversation> filteredList = new FilteredList<>(conversationObservableList, p -> true);
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

	public void addConversation(Conversation conversation) {
		conversationObservableList.add(conversation);
	}


	public void setConversationScreen(Pane pane) {
		GUI_Util.setChildToParentAnchorPane(chatScreenCenter, pane);
	}
}
