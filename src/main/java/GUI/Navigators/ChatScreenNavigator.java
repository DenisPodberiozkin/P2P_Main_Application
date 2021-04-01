package GUI.Navigators;

import GUI.Chat.ChatScreen;

public class ChatScreenNavigator extends Navigator {
	private static ChatScreen chatScreenController;


	public static void setChatScreenController(ChatScreen chatScreenController) {
		ChatScreenNavigator.chatScreenController = chatScreenController;
	}

	public static void changeConversationScreen(NavigablePane pane) {
		chatScreenController.setConversationScreen(loadPane(pane));
	}
}
