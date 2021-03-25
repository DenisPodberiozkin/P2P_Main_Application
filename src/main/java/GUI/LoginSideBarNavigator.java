package GUI;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.util.logging.Logger;

public class LoginSideBarNavigator {
    public static final String LOGIN_FORM_XML = "../fxml/LoginForm.fxml";
    public static final String LOG_ON_FORM_XML = "../fxml/LogonForm.fxml";
    private static final Logger LOGGER = Logger.getLogger(LoginSideBarNavigator.class.getName());
    private static Pane loginForm;
    private static Pane logonForm;
    private static Login loginController;

    public static void setLoginController(Login loginController) {
        LoginSideBarNavigator.loginController = loginController;
    }

    public static void changeSideBar(String fxml) {
        loginController.setSideBar(loadPane(fxml));
    }

    private static Pane loadPane(String fxml) throws IllegalArgumentException {
        try {
            switch (fxml) {
                case LOGIN_FORM_XML:
                    if (loginForm == null) {
                        loginForm = FXMLLoader.load(LoginSideBarNavigator.class.getResource(fxml));
                    }
                    return loginForm;
                case LOG_ON_FORM_XML:
                    if (logonForm == null) {
                        logonForm = FXMLLoader.load(LoginSideBarNavigator.class.getResource(fxml));
                    }
                    return logonForm;
            }
        } catch (IOException ioException) {
            LOGGER.severe("Unable to load side bar in Login Side Bar Navigator. Reason " + ioException.toString());
        }
        throw new IllegalArgumentException("There is no fxml - " + fxml);
    }

}
