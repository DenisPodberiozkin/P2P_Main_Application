package GUI.LoginScreen;

import GUI.Dialogs.ErrorAlert;
import GUI.GUI_Util;
import GUI.Navigators.LoginSideBarNavigator;
import GUI.Navigators.NavigablePane;
import User.IMainController;
import User.MainController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;

import java.io.FileNotFoundException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.ResourceBundle;

public class LoginForm implements Initializable {


    // FXML Fields

    private final LinkedList<TextInputControl> mandatoryFields = new LinkedList<>();
    @FXML
    private PasswordField passField;
    @FXML
    public TextField usernameField;

    // Normal Fields
    @FXML
    private PasswordField secretPassField;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mandatoryFields.add(passField);
        mandatoryFields.add(secretPassField);
        mandatoryFields.add(usernameField);
    }

    @FXML
    private void login() {
        final String password = passField.getText();
        final String secretPassword = secretPassField.getText();
        final String username = usernameField.getText();
        try {
            if (GUI_Util.checkMandatoryFields(mandatoryFields)) {
                IMainController mainController = MainController.getInstance();

                mainController.loginToAccount(password, secretPassword, username);
            }
        } catch (FileNotFoundException fileNotFoundException) {
            new ErrorAlert("User not found", "Username " + username + " not found", "User with username " + username + " does not exist").show();
        } catch (SQLException throwables) {
            new ErrorAlert("Database Error", "Unable to connect to user database", "Unable to connect to user database. Reason - " + throwables.getMessage()).show();
        } catch (GeneralSecurityException e) {
            new ErrorAlert("Incorrect credentials", "Password or secret password are incorrect", "Unable to decrypt user information with entered credentials").show();
        }
    }

    @FXML
    private void showLogOnForm() {
        LoginSideBarNavigator.changeSideBar(NavigablePane.LOG_ON_FORM_XML);
        clearFields();
        GUI_Util.clearMandatoryFieldsStyles(mandatoryFields);
    }

    private void clearFields() {
        usernameField.clear();
        passField.clear();
        secretPassField.clear();
    }


}
