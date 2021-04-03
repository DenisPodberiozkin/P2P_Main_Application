package GUI;

import GUI.Chat.AddConversationDialogContent;
import GUI.Chat.ChatScreen;
import GUI.Chat.ConversationScreen;
import GUI.RegistrationCarousel.FinishSlide;
import GUI.RegistrationCarousel.RegistrationCarousel;
import GUI.RegistrationCarousel.SecretPasswordSlide;

public class ControllerFactory {

	private static Debugger debuggerController;
	private static RegistrationCarousel registrationCarouselController;
	private static SecretPasswordSlide secretPasswordSlideController;
	private static FinishSlide finishSlideController;
	private static ChatScreen chatScreenController;
	private static ConversationScreen conversationScreenController;
	private static AddConversationDialogContent addConversationDialogContentController;

	public static Debugger getDebuggerController() {
		return debuggerController;
	}

	public static void setDebuggerController(Debugger debugger) {
		ControllerFactory.debuggerController = debugger;
	}

    public static RegistrationCarousel getRegistrationCarouselController() {
        return registrationCarouselController;
    }

    public static void setRegistrationCarouselController(RegistrationCarousel registrationCarouselController) {
        ControllerFactory.registrationCarouselController = registrationCarouselController;
    }

    public static SecretPasswordSlide getSecretPasswordSlideController() {
        return secretPasswordSlideController;
    }

    public static void setSecretPasswordSlideController(SecretPasswordSlide secretPasswordSlideController) {
	    ControllerFactory.secretPasswordSlideController = secretPasswordSlideController;
    }

	public static FinishSlide getFinishSlideController() {
		return finishSlideController;
	}

	public static void setFinishSlideController(FinishSlide finishSlideController) {
		ControllerFactory.finishSlideController = finishSlideController;
	}

	public static ChatScreen getChatScreenController() {
		return chatScreenController;
	}

	public static void setChatScreenController(ChatScreen chatScreenController) {
		ControllerFactory.chatScreenController = chatScreenController;
	}

	public static ConversationScreen getConversationScreenController() {
		return conversationScreenController;
	}

	public static void setConversationScreenController(ConversationScreen conversationScreenController) {
		ControllerFactory.conversationScreenController = conversationScreenController;
	}

	public static AddConversationDialogContent getAddConversationDialogContentController() {
		return addConversationDialogContentController;
	}

	public static void setAddConversationDialogContentController(AddConversationDialogContent addConversationDialogContentController) {
		ControllerFactory.addConversationDialogContentController = addConversationDialogContentController;
	}
}
