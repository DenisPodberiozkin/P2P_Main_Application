package GUI.Navigators;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.util.logging.Logger;

public abstract class Navigator {
    private static final Logger LOGGER = Logger.getLogger(Navigator.class.getName());
    private static Pane loginForm;
    private static Pane logonForm;
    private static Pane registrationCarousel;
    private static Pane loginScreen;
    private static Pane settingsPane;

    protected static Pane loadPane(NavigablePane pane) throws IllegalArgumentException {
        final String paneXml = pane.getXml();
        try {
            switch (pane) {
                case LOGIN_FORM_XML:
                    if (loginForm == null) {
                        loginForm = FXMLLoader.load(LoginSideBarNavigator.class.getResource(paneXml));
                    }
                    return loginForm;
                case LOG_ON_FORM_XML:
                    if (logonForm == null) {
                        logonForm = FXMLLoader.load(LoginSideBarNavigator.class.getResource(paneXml));
                    }
                    return logonForm;
                case REGISTRATION_CAROUSEL_XML:
                    if (registrationCarousel == null) {
                        registrationCarousel = FXMLLoader.load(LoginSideBarNavigator.class.getResource(paneXml));
                    }
                    return registrationCarousel;
                case LOGIN_XML:
                    if (loginScreen == null) {
                        loginScreen = FXMLLoader.load(LoginSideBarNavigator.class.getResource(paneXml));
                    }
                    return loginScreen;
                case SETTINGS_XML:
                    if (settingsPane == null) {
                        settingsPane = FXMLLoader.load(LoginSideBarNavigator.class.getResource(paneXml));
                    }
                    return settingsPane;
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
            LOGGER.severe("Unable to load navigable pane" + pane + ". Reason " + ioException.toString());
        }
        throw new IllegalArgumentException("There is no fxml - " + paneXml);
    }

}
