package GUI.LoginScreen;

import GUI.ControllerFactory;
import GUI.GUI_Util;
import GUI.Navigators.LoginSideBarNavigator;
import GUI.Navigators.NavigablePane;
import GUI.Navigators.StartScreenNavigator;
import User.RegistrationModel;
import User.RegistrationModelBuilder;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;

import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;

public class LogOnForm implements Initializable {
    private final LinkedList<TextInputControl> mandatoryFields = new LinkedList<>();

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passField;
    @FXML
    private PasswordField repeatPasswordField;
    @FXML
    private ProgressBar passProgressBar;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mandatoryFields.add(passField);
        mandatoryFields.add(repeatPasswordField);
        mandatoryFields.add(usernameField);
        GUI_Util.setUpPasswordFieldPair(passField, repeatPasswordField, passProgressBar);
    }

    @FXML
    private void logOn(ActionEvent actionEvent) {
        final String password = passField.getText();
        final String repeatPassword = repeatPasswordField.getText();
        final String username = usernameField.getText();


        if (GUI_Util.checkMandatoryFields(mandatoryFields) && password.equals(repeatPassword)) {
            StartScreenNavigator.changeMainScreen(NavigablePane.REGISTRATION_CAROUSEL_XML);
            RegistrationModel registrationModel = new RegistrationModelBuilder()
                    .setPassword(password)
                    .setUsername(username)
                    .createRegistrationModel();
            ControllerFactory.getSecretPasswordSlideController().setRegistrationModel(registrationModel);
            ControllerFactory.getFinishSlideController().setRegistrationModel(registrationModel);
            clearFields();
        }
    }

    @FXML
    private void showLoginForm(ActionEvent actionEvent) {
        LoginSideBarNavigator.changeSideBar(NavigablePane.LOGIN_FORM_XML);
        clearFields();
        GUI_Util.clearMandatoryFieldsStyles(mandatoryFields);

    }

    private void clearFields() {
        usernameField.clear();
        passField.clear();
        repeatPasswordField.clear();
    }

}
