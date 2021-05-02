package GUI.RegistrationCarousel;

import GUI.ControllerFactory;
import GUI.Dialogs.ErrorAlert;
import GUI.Dialogs.InformationAlert;
import GUI.Navigators.LoginSideBarNavigator;
import GUI.Navigators.NavigablePane;
import GUI.Navigators.StartScreenNavigator;
import User.Database.DatabaseExceptions.DatabaseInitException;
import User.Main.IMainController;
import User.Main.MainController;
import User.Main.RegistrationModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Optional;
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
    private InformationAlert informationAlert;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ControllerFactory.setFinishSlideController(this);
        passwordFields.add(hiddenPasswordField);
        passwordFields.add(hiddenSecretPasswordField);

        informationAlert = new InformationAlert("Registration successful", "Account has been created successfully", "");

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
        try {
            final IMainController mainController = MainController.getInstance();
            final String password = registrationModel.getPassword();
            final String secretePassword = registrationModel.getSecretePassword();
            final String username = registrationModel.getUsername();
            mainController.createAccount(password, secretePassword, username);

            showInformationAlert();

        } catch (IOException ioException) {
            new ErrorAlert("Database Error", "Unable to create database", "Unable to create database to store user data. Reason - " + ioException.getMessage()).show();
        } catch (SQLException throwables) {
            new ErrorAlert("Database Error", "Unable to connect to database", "Unable to connect to user database. Reason - " + throwables.getMessage()).show();
        } catch (DatabaseInitException e) {
            new ErrorAlert("Database Error", "Unable to initialize database", "Unable to initialize user database. Reason - " + e.getMessage()).show();
        }
    }

    private void showInformationAlert() {
        Optional<ButtonType> result = informationAlert.showAndWait();
        if (!result.isPresent() || result.get() == ButtonType.OK) {
            StartScreenNavigator.changeMainScreen(NavigablePane.LOGIN_XML);
            LoginSideBarNavigator.changeSideBar(NavigablePane.LOGIN_FORM_XML);
            ControllerFactory.getRegistrationCarouselController().resetCarousel();
        }
    }
}
