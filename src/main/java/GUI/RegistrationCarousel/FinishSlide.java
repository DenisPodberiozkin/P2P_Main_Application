package GUI.RegistrationCarousel;

import GUI.ControllerFactory;
import User.RegistrationModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;

public class FinishSlide implements Initializable, SlideController {

    private final LinkedList<PasswordField> passwordFields = new LinkedList<>();
    private RegistrationModel registrationModel;
    @FXML
    private TextField usernameField;

    @FXML
    private TextField passwordField;

    @FXML
    private TextField secretPasswordField;

    @FXML
    private PasswordField hiddenPasswordField;

    @FXML
    private PasswordField hiddenSecretPasswordField;

    @FXML
    private CheckBox showPasswordCheckBox;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ControllerFactory.setFinishSlideController(this);
        passwordFields.add(hiddenPasswordField);
        passwordFields.add(hiddenSecretPasswordField);
    }

    @Override
    public void notifyCurrentSlide() {
        final String password = registrationModel.getPassword();
        final String secretePassword = registrationModel.getSecretePassword();
        final String username = registrationModel.getUsername();


        usernameField.setText(username);
        passwordField.setText(password);
        hiddenPasswordField.setText(password);
        secretPasswordField.setText(secretePassword);
        hiddenSecretPasswordField.setText(secretePassword);
    }

    @Override
    public void notifyCancel() {
        showPasswordCheckBox.setSelected(false);
        hidePasswords();
    }

    public void setRegistrationModel(RegistrationModel registrationModel) {
        this.registrationModel = registrationModel;
    }

    private void showPasswords() {
        for (PasswordField field : passwordFields) {
            field.setVisible(false);
        }
    }


    private void hidePasswords() {
        for (PasswordField field : passwordFields) {
            field.setVisible(true);
        }
    }

    @FXML
    void passwordCheckBoxAction() {
        if (showPasswordCheckBox.isSelected()) {
            showPasswords();
        } else {
            hidePasswords();
        }
    }

    @FXML
    void finishAction() {
        System.out.println("Registration");
        //TODO registration
    }
}
