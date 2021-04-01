package GUI;

import GUI.Chat.ChatScreen;
import GUI.Chat.ConversationScreen;
import GUI.LoginScreen.LogOnForm;
import GUI.LoginScreen.Login;
import GUI.LoginScreen.LoginForm;
import GUI.RegistrationCarousel.FinishSlide;
import GUI.RegistrationCarousel.RegistrationCarousel;
import GUI.RegistrationCarousel.SecretPasswordSlide;

public class ControllerFactory {

	private static TestController testController;
	private static StartScreen startScreenController;
	private static Login loginController;
	private static LoginForm loginFormController;
	private static LogOnForm logOnFormController;
	private static RegistrationCarousel registrationCarouselController;
	private static SecretPasswordSlide secretPasswordSlideController;
	private static FinishSlide finishSlideController;
	private static ChatScreen chatScreenController;
	private static ConversationScreen conversationScreenController;

	public static TestController getTestController() {
		return testController;
	}

	public static void setTestController(TestController testController) {
		ControllerFactory.testController = testController;
	}

	public static StartScreen getStartScreenController() {
        return startScreenController;
    }

    public static void setStartScreenController(StartScreen startScreenController) {
        ControllerFactory.startScreenController = startScreenController;
    }

    public static Login getLoginController() {
        return loginController;
    }

    public static void setLoginController(Login loginController) {
        ControllerFactory.loginController = loginController;
    }

    public static LoginForm getLoginFormController() {
        return loginFormController;
    }

    public static void setLoginFormController(LoginForm loginFormController) {
        ControllerFactory.loginFormController = loginFormController;
    }

    public static LogOnForm getLogOnFormController() {
        return logOnFormController;
    }

    public static void setLogOnFormController(LogOnForm logOnFormController) {
        ControllerFactory.logOnFormController = logOnFormController;
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
}
