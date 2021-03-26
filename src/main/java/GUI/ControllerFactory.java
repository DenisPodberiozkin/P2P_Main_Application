package GUI;

import GUI.LoginScreen.LogOnForm;
import GUI.LoginScreen.Login;
import GUI.LoginScreen.LoginForm;

public class ControllerFactory {

    private static TestController testController;
    private static StartScreen startScreenController;
    private static Login loginController;
    private static LoginForm loginFormController;
    private static LogOnForm logOnFormController;

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
}
